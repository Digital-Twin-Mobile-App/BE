package com.project.dadn.producers;

import com.project.dadn.configurations.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageUploadProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public ImageUploadProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendImagePath(String filePath) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.IMAGE_UPLOAD_QUEUE, filePath);
        System.out.println("Sent file path to RabbitMQ: " + filePath);
    }
}
