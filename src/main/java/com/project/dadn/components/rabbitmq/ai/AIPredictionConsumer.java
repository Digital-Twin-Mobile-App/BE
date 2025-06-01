package com.project.dadn.components.rabbitmq.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dadn.enums.PlantStage;
import com.project.dadn.models.Image;
import com.project.dadn.models.Plant;
import com.project.dadn.repositories.ImageRepository;
import com.project.dadn.repositories.PlantRepository;
import com.project.dadn.services.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIPredictionConsumer {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${ai.service.url}")
    private String aiServiceUrl;
    private final ImageRepository imageRepository;
    private final PlantRepository plantRepository;
    private final NotificationService notificationService;

    @RabbitListener(queues = "ai.prediction.queue", concurrency = "1")
    public void processAIPrediction(Map<String, Object> message) {
        String imagePath = null;
        File imageFile = null;

        try {
            imagePath = (String) message.get("imagePath");
            UUID imageId = UUID.fromString((String) message.get("imageId"));

            log.info("Processing AI prediction for image: {}", imageId);

            imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                log.error("Image file not found: {}", imagePath);
                return;
            }

            // Call AI service with the original file
            Map<String, Object> aiResponse = callAIService(imageFile);
            if (aiResponse != null) {
                updateImageWithPrediction(imageId, aiResponse);
            }
        } catch (Exception e) {
            log.error("Error processing AI prediction: {}", e.getMessage(), e);
        } finally {
            cleanupFile(imageFile, imagePath);
        }
    }

    private Map<String, Object> callAIService(File imageFile) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(imageFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    aiServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> aiResponse = mapper.readValue(
                        response.getBody(),
                        Map.class
                );
                log.info("AI Response: {}", aiResponse);
                return aiResponse;
            }
        } catch (Exception e) {
            log.error("Error calling AI service: {}", e.getMessage(), e);
        }
        return null;
    }

    private void cleanupFile(File imageFile, String imagePath) {
        try {
            if (imageFile != null && imageFile.exists()) {
                boolean deleted = imageFile.delete();
                if (!deleted) {
                    log.warn("Could not delete temp file: {}", imagePath);
                } else {
                    log.debug("Successfully deleted temp file: {}", imagePath);
                }
            }
        } catch (Exception e) {
            log.error("Error cleaning up temp file: {}", e.getMessage());
        }
    }

    @Transactional
    public void updateImageWithPrediction(UUID imageId, Map<String, Object> aiResponse) {
        try {
            Image image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));

            Map<String, Object> prediction = (Map<String, Object>) aiResponse.get("prediction");

            PlantStage newStage = mapAIStageToPlantStage((String) prediction.get("stage"));
            Double confidence = ((Number) prediction.get("confidence")).doubleValue();
            Double heightRatio = ((Number) prediction.get("height_ratio")).doubleValue();
            String species = (String) prediction.get("species");

            image.setPlantStage(newStage);
            image.setStageConfidence(confidence);
            image.setHeightRatio(heightRatio);
            image.setDetectedSpecies(species);

            imageRepository.save(image);

            Plant plant = image.getPlant();
            if (plant != null && shouldUpdatePlant(plant, newStage, confidence)) {
                PlantStage oldPlantStage = plant.getCurrentStage();

                plant.setCurrentStage(newStage);
                plant.setLastStageConfidence(confidence);
                plantRepository.save(plant);

                // Create notification if stage changed
                if (oldPlantStage != newStage && newStage != PlantStage.UNKNOWN) {
                    notificationService.createStageChangeNotification(
                            plant,
                            oldPlantStage,
                            newStage
                    );
                }
            }

            log.info("Successfully updated image and plant with AI prediction results. ImageId: {}, Stage: {}, Confidence: {}",
                    image.getId(), newStage, confidence);
        } catch (Exception e) {
            log.error("Error in updateImageWithPrediction: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }

    private boolean shouldUpdatePlant(Plant plant, PlantStage newStage, Double newConfidence) {
        // If plant has no stage yet, and new stage has confidence > 0.5
        if (plant.getCurrentStage() == null && newConfidence != null && newConfidence > 0.5) {
            log.info("Plant has no stage, updating to stage: {}, confidence: {}", newStage, newConfidence);
            return true;
        }

        log.info("Plant stage: {}, confidence: {}", plant.getCurrentStage(), plant.getLastStageConfidence());

        return newStage != plant.getCurrentStage() &&
                newConfidence != null &&
                newConfidence > 0.5;
    }

    private PlantStage mapAIStageToPlantStage(String aiStage) {
        if (aiStage == null) return PlantStage.UNKNOWN;

        return switch (aiStage) {
            case "Mới nảy mầm (stage_1)" -> PlantStage.GERMINATION;
            case "Mầm đang phát triển (stage_2)" -> PlantStage.VEGETATION;
            case "Mầm trưởng thành (stage_3)" -> PlantStage.FLOWERING;
            default -> PlantStage.UNKNOWN;
        };
    }
}