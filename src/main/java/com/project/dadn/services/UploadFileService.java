package com.project.dadn.services;

import com.project.dadn.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UploadFileService {

    private final ImageRepository imageRepository;

    
}
