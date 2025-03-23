package com.project.dadn.services;

import com.project.dadn.dtos.requests.VerifyOTPRequest;
import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.utlls.OtpUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OtpService {

    StringRedisTemplate redisTemplate;
    private final OtpUtil otpUtil;

    public AuthenticationResponse verifyOtp(VerifyOTPRequest request) {
        String email = request.getEmail();
        String redisKey = "otp:" + email;
        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        if (storedOtp == null) {
            throw new AppException(ErrorCodes.OTP_INVALID);
        }

        if (!storedOtp.equals(request.getOtp())) {
            throw new AppException(ErrorCodes.OTP_EXPIRED);
        }

        redisTemplate.delete(redisKey);

        return otpUtil.saveOtpUser(email);
    }
}
