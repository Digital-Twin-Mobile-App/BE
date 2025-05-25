package com.project.dadn.services;

import com.project.dadn.dtos.responses.PlantResponse;
import com.project.dadn.mappers.PlantMapper;
import com.project.dadn.models.Image;
import com.project.dadn.models.Plant;
import com.project.dadn.models.User;
import com.project.dadn.repositories.ImageRepository;
import com.project.dadn.repositories.PlantRepository;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.utlls.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UploadFileService uploadFileService;
    private final PlantMapper plantMapper;

    @Transactional
    public PlantResponse addPlantWithCover(MultipartFile coverImageFile, HttpServletRequest request) throws ParseException {
        String email = jwtUtil.getEmailToken(jwtUtil.getUserToken(request));
        User user = userRepository.findByEmail(email).orElseThrow();

        Plant plant = new Plant();
        plant.setOwner(user);

        PlantResponse response = plantMapper.toResponse(plantRepository.save(plant));

        if (!coverImageFile.isEmpty()) {
            try {

                String coverFileName = UUID.randomUUID() + "_" + coverImageFile.getOriginalFilename();
                String baseDir = System.getProperty("user.dir") + "/uploads/";
                java.io.File dir = new java.io.File(baseDir);
                if (!dir.exists()) dir.mkdirs();

                java.io.File coverTempFile = new java.io.File(baseDir + coverFileName);
                coverImageFile.transferTo(coverTempFile);

                uploadFileService.uploadCoverImgAsync(plant, coverTempFile);

                plantRepository.save(plant);

            } catch (Exception e) {
                System.err.println("Async cover image upload failed: " + e.getMessage());
            }
        }

        return response;
    }


}
