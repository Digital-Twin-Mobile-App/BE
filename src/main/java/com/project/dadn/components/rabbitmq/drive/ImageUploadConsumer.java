package com.project.dadn.components.rabbitmq.drive;

import com.project.dadn.services.UploadFileService;
import com.project.dadn.configurations.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ImageUploadConsumer {

    private final UploadFileService uploadFileService;

    @Autowired
    public ImageUploadConsumer(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    @RabbitListener(queues = RabbitMQConfig.IMAGE_UPLOAD_QUEUE)
    public void receiveMessage(String filePath) {
        System.out.println("Received file path from RabbitMQ: " + filePath);
        try {
            File file = new File(filePath);
            if (file.exists()) {
                uploadFileService.uploadImageToDrive(file);
            } else {
                System.err.println("File not found: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
