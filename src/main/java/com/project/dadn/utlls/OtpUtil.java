package com.project.dadn.utlls;

import com.project.dadn.dtos.requests.VerifyOTPRequest;
import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.mappers.UserMapper;
import com.project.dadn.models.User;
import com.project.dadn.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class OtpUtil {

    private static final SecureRandom random = new SecureRandom();
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TokenUtil tokenUtil;
    private final PasswordEncoder passwordEncoder;



    public static String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }

    public void getOtpKey(String email) {
        String otpKey = "OTP:" + email;
        String storedOtp = redisTemplate.opsForValue().get(otpKey);

        if (storedOtp == null) {
            throw new AppException(ErrorCodes.OTP_EXPIRED);
        }
    }

    public AuthenticationResponse saveOtpUser(String email) {
        if (!userRepository.existsByEmail(email)) {
            User newUser = new User();
            newUser.setEmail(email);
            userRepository.save(newUser);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCodes.UNAUTHENTICATED));

        String token = tokenUtil.generateToken(user);
        return AuthenticationResponse.buildAuthenticationResponse(token, true);
    }

}
