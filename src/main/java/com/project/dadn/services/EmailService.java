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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {

    EmailProducer emailProducer;
    static int OTP_TTL = 3;
    StringRedisTemplate redisTemplate;

    public void sendEmailReset(VerifyEmailRequest request) {
        String email = request.getEmail();
        String redisKey = "otp:" + email;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new AppException(ErrorCodes.OTP_IN_USED);
        }

        String otp = OtpUtil.generateOtp();

        redisTemplate.opsForValue().set(redisKey, otp, OTP_TTL, TimeUnit.MINUTES);

        String msgBody =
                "Your OTP:" + otp ;

        EmailDetailRequest details = EmailDetailRequest.builder()
                .subject("Reset Password OTP")
                .msgBody(msgBody)
                .recipient("vietlh0207@gmail.com")
                .build();

        emailProducer.processEmail(details);
        log.info("Email OTP sent");
    }

}
