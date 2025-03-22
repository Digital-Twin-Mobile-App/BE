package com.project.dadn.components.rabbitmq.email;


import com.project.dadn.configurations.RabbitMQConfig;
import com.project.dadn.dtos.requests.EmailDetailRequest;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;

    public void processEmail(EmailDetailRequest details) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, details);
        } catch (Exception e) {
            throw new AppException(ErrorCodes.RABBITMQ_ERROR);
        }
    }
}

