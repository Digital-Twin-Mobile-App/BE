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
import com.project.dadn.producers.ImageUploadProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UploadFileService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.drive.credentials.path}")
    private String credentialsPath;

    @Value("${google.drive.folder-id}")
    private String folderId;

    private final ImageUploadProducer imageUploadProducer;

    /**
     * Xử lý upload file lên hệ thống
     */
    public APIResponse<String> uploadFile(MultipartFile file) {
        try {
            java.io.File tempFile = saveFileToLocal(file);
            if (tempFile == null) {
                throw new AppException(ErrorCodes.FILE_NOT_FOUND);
            }

            imageUploadProducer.sendImagePath(tempFile.getAbsolutePath());

            return APIResponse.<String>builder()
                    .code(200)
                    .result("File uploaded to queue successfully!")
                    .build();

        } catch (IOException e) {
            throw new AppException(ErrorCodes.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Lưu file tạm vào thư mục cục bộ
     */
    private java.io.File saveFileToLocal(MultipartFile file) throws IOException {
        String baseDirectory = System.getProperty("user.dir") + "/uploads/";
        java.io.File directory = new java.io.File(baseDirectory);

        // Tạo thư mục nếu chưa tồn tại
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Định nghĩa đường dẫn đầy đủ của file
        String filePath = baseDirectory + file.getOriginalFilename();
        java.io.File localFile = new java.io.File(filePath);

        // Lưu file vào thư mục
        file.transferTo(localFile);
        System.out.println("File saved at: " + localFile.getAbsolutePath());
        return localFile;
    }

    /**
     * Upload file lên Google Drive
     */
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

        // Xóa file sau khi upload thành công
        if (file.delete()) {
            System.out.println("Deleted file: " + file.getAbsolutePath());
        } else {
            System.err.println("Failed to delete file: " + file.getAbsolutePath());
        }
    }

    /**
     * Tạo kết nối với Google Drive
     */
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
}
