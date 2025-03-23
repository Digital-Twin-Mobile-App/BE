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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OtpService {

    StringRedisTemplate redisTemplate;
    private final TokenUtil tokenUtil;
    private final UserRepository userRepository;

    public AuthenticationResponse verifyOtp(String otp, String email) {
        String redisKey = "otp:" + email;
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCodes.UNAUTHENTICATED));

        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new AppException(ErrorCodes.OTP_INVALID);
        }

        // Xóa OTP sau khi xác minh thành công
        redisTemplate.delete(redisKey);

        return AuthenticationResponse.builder()
                .token(tokenUtil.generateToken(user))
                .authenticated(true)
                .build();
    }
}
