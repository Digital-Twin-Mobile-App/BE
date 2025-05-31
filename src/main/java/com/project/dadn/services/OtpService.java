package com.project.dadn.services;

import com.project.dadn.dtos.requests.VerifyOTPRequest;
import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.models.User;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.utlls.OtpUtil;
import com.project.dadn.utlls.TokenUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.project.dadn.services.EmailService.OTP_TTL;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OtpService {

    StringRedisTemplate redisTemplate;

    public AuthenticationResponse verifyOtp(String otp, String email) {
        String redisKey = "otp:" + email;

        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new AppException(ErrorCodes.OTP_INVALID);
        }

        redisTemplate.delete(redisKey);

        redisTemplate.opsForValue().set("otp_verified:" + email, "true", 30, TimeUnit.MINUTES);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .build();
    }
}
