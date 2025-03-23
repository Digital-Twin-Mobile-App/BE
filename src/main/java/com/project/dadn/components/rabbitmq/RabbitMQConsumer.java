package com.project.dadn.components.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.project.dadn.configurations.RabbitMQConfig;

@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handle(String message) {
        System.out.println("Handle message: " + message);
    }
}
