package com.project.dadn.services;

import com.project.dadn.components.rabbitmq.email.EmailProducer;
import com.project.dadn.dtos.requests.EmailDetailRequest;
import com.project.dadn.dtos.requests.VerifyEmailRequest;
import com.project.dadn.enums.RoleEnum;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.models.Role;
import com.project.dadn.models.User;
import com.project.dadn.repositories.RoleRepository;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.utlls.OtpUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final EmailProducer emailProducer;
    static int OTP_TTL = 8;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public void sendEmailReset(VerifyEmailRequest request) {
        String email = request.getEmail();
        String redisKey = "otp:" + email;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new AppException(ErrorCodes.OTP_IN_USED);
        }

        String otp = OtpUtil.generateOtp();

        redisTemplate.opsForValue().set(redisKey, otp, OTP_TTL, TimeUnit.MINUTES);

        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword("default_password"); // Mật khẩu mặc định

            Role role = roleRepository.findById(RoleEnum.USER.name())
                    .orElseThrow(() -> new AppException(ErrorCodes.ROLE_NOT_FOUND));

            user.setRoles(Collections.singleton(role));
            userRepository.save(user);
        }

        String msgBody = String.format(
                "<html>" +
                        "<body>" +
                        "<p style=\"font-size:18px;\">Hello,</p>" +
                        "<p style=\"font-size:16px;\">You have requested to reset your password. Please use the OTP below to proceed:</p>" +
                        "<div style=\"background-color:#f9f9f9;border:1px solid #ddd;padding:20px;text-align:center;margin-top:20px;\">" +
                        "<h3 style=\"color:#007BFF;\">Your OTP is: <span style=\"font-size:24px;font-weight:bold;\">%s</span></h3>" +
                        "</div>" +
                        "<p style=\"font-size:16px;margin-top:20px;\">If you did not request a password reset, please ignore this email.</p>" +
                        "</body>" +
                        "</html>", otp);

        EmailDetailRequest details = EmailDetailRequest.builder()
                .subject("Reset Password OTP")
                .msgBody(msgBody)
                .recipient(email)
                .build();

        emailProducer.processEmail(details);
        log.info("Email OTP sent");
    }

}
