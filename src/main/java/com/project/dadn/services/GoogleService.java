package com.project.dadn.services;

import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.models.User;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.utlls.RedisUtil;
import com.project.dadn.utlls.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;
    private final RedisUtil redisUtil;

    public AuthenticationResponse authenticateWithGoogle(OAuth2AuthenticationToken authentication) {
        OAuth2User oauth2User = authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        String token = tokenUtil.generateToken(user);

        return AuthenticationResponse.buildAuthenticationResponse(token, true);
    }

}
