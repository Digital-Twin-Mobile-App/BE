package com.project.dadn.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.project.dadn.dtos.responses.APIResponse;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
//import com.project.dadn.producers.ImageUploadProducer;
import com.project.dadn.models.Image;
import com.project.dadn.models.Plant;
import com.project.dadn.models.User;
import com.project.dadn.repositories.ImageRepository;
import com.project.dadn.repositories.PlantRepository;
import com.project.dadn.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UploadFileService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final UserRepository userRepository;
    private final PlantRepository plantRepository;
    private final ImageRepository imageRepository;

    @Value("${google.drive.credentials.path}")
    private String credentialsPath;

    @Value("${google.drive.folder-id}")
    private String folderId;

//    private final ImageUploadProducer imageUploadProducer;

    public APIResponse<String> uploadFile(MultipartFile file) {
        try {
            java.io.File tempFile = saveFileToLocal(file);
            if (tempFile == null) {
                throw new AppException(ErrorCodes.FILE_NOT_FOUND);
            }

//            imageUploadProducer.upload(tempFile.getAbsolutePath());

            return APIResponse.<String>builder()
                    .code(200)
                    .result("File uploaded to queue successfully!")
                    .build();

        } catch (IOException e) {
            throw new AppException(ErrorCodes.FILE_UPLOAD_FAILED);
        }
    }

    private java.io.File saveFileToLocal(MultipartFile file) throws IOException {
        String baseDirectory = System.getProperty("user.dir") + "/uploads/";
        java.io.File directory = new java.io.File(baseDirectory);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = baseDirectory + file.getOriginalFilename();
        java.io.File localFile = new java.io.File(filePath);

        file.transferTo(localFile);
        System.out.println("File saved at: " + localFile.getAbsolutePath());
        return localFile;
    }

    public void uploadImageToDrive(java.io.File file) throws GeneralSecurityException, IOException {
        Drive driveService = createDriveService();

        File fileMetadata = new File();
        fileMetadata.setName(file.getName());
        fileMetadata.setParents(Collections.singletonList(folderId));

        FileContent mediaContent = new FileContent("image/jpeg", file);
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        System.out.println("File uploaded successfully: " + uploadedFile.getId());

        if (file.delete()) {
            System.out.println("Deleted file: " + file.getAbsolutePath());
        } else {
            System.err.println("Failed to delete file: " + file.getAbsolutePath());
        }
    }
    private Drive createDriveService() throws GeneralSecurityException, IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(credentialsPath))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));
        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName("Image Upload Service")
                .build();
    }

    public String uploadImageToDriveAndReturnUrl(java.io.File file) throws GeneralSecurityException, IOException {
        Drive driveService = createDriveService();

        File fileMetadata = new File();
        fileMetadata.setName(file.getName());
        fileMetadata.setParents(Collections.singletonList(folderId));

        FileContent mediaContent = new FileContent("image/jpeg", file);
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        String fileId = uploadedFile.getId();
        String imageUrl = "https://drive.google.com/uc?id=" + fileId;

        if (file.delete()) {
            System.out.println("Deleted temp file: " + file.getAbsolutePath());
        }

        return imageUrl;
    }

    @Async
    public void uploadAvatarAsync(User user, java.io.File tempFile) {
        try {
            String imageUrl = uploadImageToDriveAndReturnUrl(tempFile);
            user.setAvatarUrl(imageUrl);
            userRepository.save(user); // lưu lại URL đã cập nhật
        } catch (Exception e) {
            System.err.println("Async upload failed: " + e.getMessage());
        }
    }

    @Async
    public void uploadCoverImgAsync(Plant plant, java.io.File tempFile) throws GeneralSecurityException, IOException {
        try {

            String imageUrl = uploadImageToDriveAndReturnUrl(tempFile);
            plant.setPlantCoverUrl(imageUrl);
            plantRepository.save(plant); // lưu lại URL đã cập nhật
        } catch (Exception e) {
            System.err.println("Async cover failed: " + e.getMessage());
        }
    }

    @Async
    public void uploadImageAsync(Image image, java.io.File file) {
            try {
                String imageUrl = uploadImageToDriveAndReturnUrl(file);
                image.setMediaUrl(imageUrl);
                imageRepository.save(image);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image to Drive", e);
            }
    }

}
