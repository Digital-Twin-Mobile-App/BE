package com.project.dadn.controllers;

import com.project.dadn.dtos.responses.APIResponse;
import com.project.dadn.dtos.responses.ImageHistoryResponse;
import com.project.dadn.dtos.responses.PlantResponse;
import com.project.dadn.enums.TreeStatus;
import com.project.dadn.enums.WateringFrequency;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.models.Image;
import com.project.dadn.models.User;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.services.ImageService;
import com.project.dadn.services.PlantService;
import com.project.dadn.utlls.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plants")
public class PlantController {
    private final PlantService plantService;
    private final ImageService imageService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public APIResponse<PlantResponse> addPlantWithCover(
            @RequestParam("image") MultipartFile coverImageFile,
            @RequestParam(required = false) String name,
            HttpServletRequest request) throws ParseException {

        PlantResponse plantResponse = plantService.addPlantWithCover(coverImageFile, request, name);

        return APIResponse.<PlantResponse>builder()
                .result(plantResponse)
                .build();
    }

    @PostMapping("/upload")
    public APIResponse<ImageHistoryResponse> addImageToPlant(
            @RequestParam UUID plantId,
            @RequestParam("image") MultipartFile imageFile,
            HttpServletRequest request) throws ParseException {

        ImageHistoryResponse savedImage = imageService.addImageToPlant(plantId, imageFile, request);

        return APIResponse.<ImageHistoryResponse>builder()
                .result(savedImage)
                .build();
    }

    @GetMapping("/history")
    public ResponseEntity<?> getPlantImageHistory(
            @RequestParam UUID plantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<?> history = imageService.getPlantImageHistory(plantId, page, size);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestImages(
            @RequestParam UUID plantId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(imageService.getLatestImages(plantId, limit));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getPlantImageStats(@RequestParam UUID plantId) {
        return ResponseEntity.ok(imageService.getPlantImageCount(plantId));
    }

    @GetMapping("/my-uploads")
    public ResponseEntity<?> getCurrentUserUploadHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws ParseException {
        String token = jwtUtil.getUserToken(request);
        String email = jwtUtil.getEmailToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(imageService.getUserPlantsWithCoverImage(user.getId(), pageable));
    }

}
