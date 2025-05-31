package com.project.dadn.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dadn.components.rabbitmq.ai.AIPredictionProducer;
import com.project.dadn.dtos.responses.ImageHistoryResponse;
import com.project.dadn.dtos.responses.PlantResponse;
import com.project.dadn.enums.PlantStage;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.mappers.ImageMapper;
import com.project.dadn.mappers.PlantMapper;
import com.project.dadn.models.Image;
import com.project.dadn.models.Plant;
import com.project.dadn.models.User;
import com.project.dadn.repositories.ImageRepository;
import com.project.dadn.repositories.PlantRepository;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.utlls.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final PlantRepository plantRepository;
    private final UploadFileService uploadFileService;
    private final JwtUtil jwtUtil;
    private final ImageMapper imageMapper;
    private final RabbitTemplate rabbitTemplate;
    private final PlantMapper plantMapper;
    private final AIPredictionProducer aiPredictionProducer;

    private final PlatformTransactionManager transactionManager;

    private void handleAsyncImageProcessing(File tempFile, Image savedImg) {
        CompletableFuture.runAsync(() -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
            transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            try {
                // 1. Gửi file cho AI prediction trước
                aiPredictionProducer.processAIPrediction(
                        tempFile.getAbsolutePath(),
                        savedImg.getId()
                );

                // 2. Sau đó mới upload lên drive
                String mediaUrl = uploadFileService.uploadImageToDriveAndReturnUrlNor(tempFile);

                // 3. Cuối cùng mới cập nhật URL trong database
                transactionTemplate.execute(status -> {
                    try {
                        Image imageToUpdate = imageRepository.findById(savedImg.getId())
                                .orElseThrow();
                        imageToUpdate.setMediaUrl(mediaUrl);
                        imageRepository.save(imageToUpdate);
                        return null;
                    } catch (Exception e) {
                        log.error("Error updating image URL: {}", e.getMessage());
                        status.setRollbackOnly();
                        return null;
                    }
                });
            } catch (Exception e) {
                log.error("Error in async processing: {}", e.getMessage(), e);
            }
        });
    }
    @Transactional
    public ImageHistoryResponse addImageToPlant(UUID plantId, MultipartFile imageFile, HttpServletRequest request) throws ParseException {
        String email = jwtUtil.getEmailToken(jwtUtil.getUserToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();

        // Lấy và refresh plant entity
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new AppException(ErrorCodes.PLANT_NOT_FOUND));
        plant = plantRepository.saveAndFlush(plant);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String baseDir = System.getProperty("user.dir") + "/uploads/temp/";
                File dir = new File(baseDir);
                if (!dir.exists()) dir.mkdirs();

                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                File tempFile = new File(baseDir + fileName);
                imageFile.transferTo(tempFile);

                // Lưu image với transaction hiện tại
                Image image = new Image();
                image.setMediaTitle(imageFile.getOriginalFilename());
                image.setMediaUrl("pending");
                image.setUploader(user);
                image.setPlant(plant);

                // Lưu và flush ngay để đảm bảo mọi thứ được persist
                Image savedImg = imageRepository.saveAndFlush(image);

                // Xử lý upload async
                handleAsyncImageProcessing(tempFile, savedImg);

                return imageMapper.toImageHistoryResponse(savedImg);

            } catch (Exception e) {
                log.error("Failed to upload image: {}", e.getMessage(), e);
                throw new AppException(ErrorCodes.IMAGE_UPLOAD_FAILED);
            }
        }

        throw new AppException(ErrorCodes.IMAGE_UPLOAD_FAILED);
    }

    public Page<ImageHistoryResponse> getPlantImageHistory(UUID plantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Image> imagePage = imageRepository.findByPlant_IdOrderByIdDesc(plantId, pageable);
        return imageMapper.toImageHistoryResponsePage(imagePage);
    }

    public List<ImageHistoryResponse> getLatestImages(UUID plantId, int limit) {
        List<Image> imagePage = imageRepository.findLatestImagesByPlantId(plantId, PageRequest.of(0, limit));
        return imageMapper.toImageHistoryResponseList(imagePage);
    }

    public Long getPlantImageCount(UUID plantId) {
        return imageRepository.countByPlant_Id(plantId);
    }

    public Page<ImageHistoryResponse> getUserUploadHistory(UUID userId, Pageable pageable) {
        Page<Image> imagePage = imageRepository.findByUploader_IdOrderByIdDesc(userId, pageable);
        return imageMapper.toImageHistoryResponsePage(imagePage);
    }

    public Page<PlantResponse> getUserPlantsWithCoverImage(UUID userId, Pageable pageable) {
        Page<Plant> plants = plantRepository.findByOwner_Id(userId, pageable);
        return plants.map(plantMapper::toResponse);
    }


}
