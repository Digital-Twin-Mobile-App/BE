package com.project.dadn.services;

import com.project.dadn.dtos.responses.ImageHistoryResponse;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.mappers.ImageMapper;
import com.project.dadn.models.Image;
import com.project.dadn.models.Plant;
import com.project.dadn.models.User;
import com.project.dadn.repositories.ImageRepository;
import com.project.dadn.repositories.PlantRepository;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.utlls.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final PlantRepository plantRepository;
    private final UploadFileService uploadFileService;
    private final JwtUtil jwtUtil;
    private final ImageMapper imageMapper;

    @Transactional
    public Image addImageToPlant(UUID plantId, MultipartFile imageFile, HttpServletRequest request) throws ParseException, ParseException {
        String email = jwtUtil.getEmailToken(jwtUtil.getUserToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();

        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new AppException(ErrorCodes.PLANT_NOT_FOUND));

        // Tạo entity Image mới
        Image image = new Image();
        image.setMediaTitle(imageFile.getOriginalFilename());
        image.setUploader(user);
        image.setPlant(plant);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                String baseDir = System.getProperty("user.dir") + "/uploads/";
                java.io.File dir = new java.io.File(baseDir);
                if (!dir.exists()) dir.mkdirs();

                java.io.File tempFile = new java.io.File(baseDir + fileName);
                imageFile.transferTo(tempFile);

                uploadFileService.uploadImageAsync(image, tempFile);

                // Lưu image vào database
                return imageRepository.save(image);

            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }

        throw new AppException(ErrorCodes.IMAGE_UPLOAD_FAILED);
    }

    public Page<Image> getPlantImageHistory(UUID plantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return imageRepository.findByPlant_IdOrderByIdDesc(plantId, pageable);
    }

    public List<Image> getLatestImages(UUID plantId, int limit) {
        return imageRepository.findLatestImagesByPlantId(plantId, PageRequest.of(0, limit));
    }

    public Long getPlantImageCount(UUID plantId) {
        return imageRepository.countByPlant_Id(plantId);
    }

    public Page<ImageHistoryResponse> getUserUploadHistory(UUID userId, Pageable pageable) {
        Page<Image> imagePage = imageRepository.findByUploader_IdOrderByIdDesc(userId, pageable);
        return imageMapper.toImageHistoryResponsePage(imagePage);
    }
}
