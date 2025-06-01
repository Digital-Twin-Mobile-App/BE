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
    private final PlantMapper plantMapper;
    private final AIPredictionProducer aiPredictionProducer;

    private final PlatformTransactionManager transactionManager;

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
                String coverFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                String baseDir = System.getProperty("user.dir") + "/uploads/";
                java.io.File dir = new java.io.File(baseDir);
                if (!dir.exists()) dir.mkdirs();

                java.io.File coverTempFile = new java.io.File(baseDir + coverFileName);
                imageFile.transferTo(coverTempFile);

                String mediaUrl = uploadFileService.uploadImageToDriveAndReturnUrlNor(coverTempFile);

                // Lưu image với transaction hiện tại
                Image image = new Image();
                image.setMediaTitle(imageFile.getOriginalFilename());
                image.setMediaUrl(mediaUrl);
                image.setUploader(user);
                image.setPlant(plant);

                Image savedImg = imageRepository.save(image);

                // 1. Gửi file cho AI prediction trước
                aiPredictionProducer.processAIPrediction(
                        coverTempFile.getAbsolutePath(),
                        savedImg.getId()
                );

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
        Page<Image> imagePage = imageRepository.findByPlant_IdOrderByUpdatedAtDesc(plantId, pageable);
        return imageMapper.toImageHistoryResponsePage(imagePage);
    }

    public ImageHistoryResponse getLatestImages(UUID plantId, int limit) {
        Image imagePage = imageRepository.findLatestImagesByPlantId(plantId);
        return imageMapper.toImageHistoryResponse(imagePage);
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
