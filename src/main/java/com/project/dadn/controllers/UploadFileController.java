package com.project.dadn.controllers;

import com.project.dadn.dtos.responses.APIResponse;
import com.project.dadn.services.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UploadFileController {

    private final UploadFileService uploadFileService;

    @PostMapping("/uploadImage")
    public APIResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return uploadFileService.uploadFile(file);
    }
}



