package com.project.dadn.configurations;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitMqHost;

    @Value("${spring.rabbitmq.port}")
    private String rabbitMqPort;

    public static final String EMAIL_QUEUE = "email_queue";
    public static final String IMAGE_UPLOAD_QUEUE = "image_upload_queue";
    public static final String AVATAR_UPLOAD_QUEUE = "avatar_upload_queue";

    // Tạo hàng đợi cho email
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    // Tạo hàng đợi cho upload ảnh
    @Bean
    public Queue imageUploadQueue() {
        return QueueBuilder.durable(IMAGE_UPLOAD_QUEUE)
                .ttl(60000)  // 60 seconds timeout for messages
                .build();
    }

    @Bean
    public Queue avatarUploadQueue() {
        return QueueBuilder.durable(AVATAR_UPLOAD_QUEUE)
                .ttl(30000)
                .build();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMqHost);
        connectionFactory.setPort(Integer.parseInt(rabbitMqPort));

        connectionFactory.setRequestedHeartBeat(60);
        connectionFactory.setConnectionTimeout(5000);

        return connectionFactory;
    }

    // Bộ chuyển đổi JSON
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Cấu hình RabbitTemplate để sử dụng Jackson
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Cấu hình Listener Container Factory để sử dụng Jackson
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }


}
