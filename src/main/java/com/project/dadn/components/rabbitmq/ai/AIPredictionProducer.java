package com.project.dadn.components.rabbitmq.ai;

import com.project.dadn.configurations.RabbitMQConfig;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIPredictionProducer {
    private final RabbitTemplate rabbitTemplate;

    public void processAIPrediction(String imagePath, UUID imageId) {
        try {
            File file = new File(imagePath);
            if (!file.exists()) {
                log.error("File not found before sending to queue: {}", imagePath);
                throw new AppException(ErrorCodes.FILE_NOT_FOUND);
            }

            Map<String, Object> message = Map.of(
                    "imagePath", imagePath,
                    "imageId", imageId.toString()
            );

            rabbitTemplate.convertAndSend(
                    "ai.prediction.exchange",
                    "ai.prediction.routing.key",
                    message
            );

            log.info("Sent image {} to AI prediction queue", imageId);
        } catch (Exception e) {
            log.error("Error sending message to AI prediction queue: {}", e.getMessage());
            throw new AppException(ErrorCodes.RABBITMQ_ERROR);
        }
    }
}