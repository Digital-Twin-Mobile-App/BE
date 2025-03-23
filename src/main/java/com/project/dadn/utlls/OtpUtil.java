package com.project.dadn.utlls;

import com.project.dadn.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class OtpUtil {

    private static final SecureRandom random = new SecureRandom();
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;

    public static String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }
}
