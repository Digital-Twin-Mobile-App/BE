package com.project.dadn.services;

import com.project.dadn.components.rabbitmq.email.EmailProducer;
import com.project.dadn.dtos.requests.EmailDetailRequest;
import com.project.dadn.dtos.requests.VerifyEmailRequest;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.utlls.OtpUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final EmailProducer emailProducer;
    static int OTP_TTL = 3;
    private final StringRedisTemplate redisTemplate;

    @Value("${link.otp}")
    private String linkOtp;


    public void sendEmailReset(VerifyEmailRequest request) {
        String email = request.getEmail();
        String redisKey = "otp:" + email;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new AppException(ErrorCodes.OTP_IN_USED);
        }

        String otp = OtpUtil.generateOtp();



        redisTemplate.opsForValue().set(redisKey, otp, OTP_TTL, TimeUnit.MINUTES);

        String verificationLink =  linkOtp + otp + "&email=" + email;

        String msgBody = String.format("<html>" + "<body>" + "<p>Click the button below to verify:</p>" + "<a href=\"%s\" style=\"display:inline-block;padding:10px 20px;background-color:#007BFF;color:white;text-decoration:none;border-radius:5px;\">Verify OTP</a>" + "</body>" + "</html>", verificationLink);

        EmailDetailRequest details = EmailDetailRequest.builder()
                .subject("Reset Password OTP")
                .msgBody(msgBody)
                .recipient(email)
                .build();

        emailProducer.processEmail(details);
        log.info("Email OTP sent");
    }

}
