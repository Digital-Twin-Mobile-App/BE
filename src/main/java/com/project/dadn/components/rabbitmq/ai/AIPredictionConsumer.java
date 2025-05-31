package com.project.dadn.components.rabbitmq.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dadn.enums.PlantStage;
import com.project.dadn.models.Image;
import com.project.dadn.models.Plant;
import com.project.dadn.repositories.ImageRepository;
import com.project.dadn.repositories.PlantRepository;
import com.project.dadn.services.NotificationService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIPredictionConsumer {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String AI_SERVICE_URL = "http://localhost:8000/predict_file/";
    private final ImageRepository imageRepository;
    private final PlantRepository plantRepository;
    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;
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

            // Call AI service outside transaction
            File tempFile = createTempCopy(imageFile);
            try {
                // Gọi AI service với file tạm
                Map<String, Object> aiResponse = callAIService(tempFile);
                if (aiResponse != null) {
                    updateImageWithTransactionalSupport(imageId, aiResponse);
                }
            } finally {
                // Dọn dẹp file tạm
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
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
                    AI_SERVICE_URL,
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

    private File createTempCopy(File originalFile) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = "ai_prediction_" + System.currentTimeMillis() + "_" + originalFile.getName();
        File tempFile = new File(tempDir, fileName);

        try (FileInputStream in = new FileInputStream(originalFile);
             FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
        return tempFile;
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

    private void updateImageWithTransactionalSupport(UUID imageId, Map<String, Object> aiResponse) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

        transactionTemplate.execute(status -> {
            try {
                Image image = imageRepository.findById(imageId)
                        .orElseThrow(() -> new RuntimeException("Image not found: " + imageId));

                updateImageDetails(image, aiResponse);

                entityManager.flush();
                return null;
            } catch (Exception e) {
                log.error("Transaction error: {}", e.getMessage(), e);
                status.setRollbackOnly();
                return null;
            } finally {
                entityManager.clear();
            }
        });
    }

    private void updateImageDetails(Image image, Map<String, Object> aiResponse) {
        try {

            Map<String, Object> prediction = (Map<String, Object>) aiResponse.get("prediction");

            Image freshImage = imageRepository.findById(image.getId())
                    .orElseThrow(() -> new RuntimeException("Image not found: " + image.getId()));


            PlantStage newStage = mapAIStageToPlantStage((String) prediction.get("stage"));
            Double confidence = ((Number) prediction.get("confidence")).doubleValue();
            Double heightRatio = ((Number) prediction.get("height_ratio")).doubleValue();
            String species = (String) prediction.get("species");

            image.setPlantStage(newStage);
            image.setStageConfidence(confidence);
            image.setHeightRatio(heightRatio);
            image.setDetectedSpecies(species);

            imageRepository.saveAndFlush(freshImage);

            Plant plant = image.getPlant();
            if (plant != null || shouldUpdatePlant(plant, newStage, confidence)) {
                PlantStage oldPlantStage = plant.getCurrentStage();

                plant.setCurrentStage(newStage);
                plant.setLastStageConfidence(confidence);
                plantRepository.saveAndFlush(plant);

                // Tạo notification trong transaction riêng
                if (oldPlantStage != newStage && newStage != PlantStage.UNKNOWN) {
                    TransactionTemplate notificationTx = new TransactionTemplate(transactionManager);
                    notificationTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

                    notificationTx.execute(status -> {
                        try {
                            notificationService.createStageChangeNotification(
                                    plant,
                                    oldPlantStage,
                                    newStage
                            );
                            return null;
                        } catch (Exception e) {
                            log.error("Error creating notification: {}", e.getMessage());
                            status.setRollbackOnly();
                            return null;
                        }
                    });
                }

            }

            // Force flush changes

            log.info("Successfully updated image and plant with AI prediction results. ImageId: {}, Stage: {}, Confidence: {}",
                    image.getId(), newStage, confidence);
        } catch (Exception e) {
            log.error("Error in updateImageDetails: {}", e.getMessage(), e);
            throw e; // Re-throw để transaction rollback
        }
    }

    private boolean shouldUpdatePlant(Plant plant, PlantStage newStage, Double newConfidence) {
        // Nếu chưa có stage, và stage mới có độ tin cậy > 0.5
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
