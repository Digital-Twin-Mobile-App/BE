package com.project.dadn.components.rabbitmq.email;


import com.project.dadn.configurations.RabbitMQConfig;
import com.project.dadn.dtos.requests.EmailDetailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    private final JavaMailSender javaMailSender;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consumeEmail(EmailDetailRequest details) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());
            helper.setText(details.getMsgBody(), true);

            javaMailSender.send(message);
            log.info("Email sent successfully to {}", details.getRecipient());
        } catch (MessagingException e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Lỗi khi gửi email", e);
        }
    }
}
