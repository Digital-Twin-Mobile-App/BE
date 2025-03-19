package com.project.dadn.services;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.SignedJWT;
import com.project.dadn.dtos.requests.*;
import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.models.User;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.utlls.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {
    UserRepository userRepository;
    JwtUtil jwtUtil;
    TokenService tokenService;
    TokenUtil tokenUtil;
    RedisTemplate<String, String> redisTemplate;
    PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    private final RedisUtil redisUtil;


    public AuthenticationResponse authenticate(AuthenticationRequest request){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCodes.UNAUTHENTICATED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                user.getPassword());

        if (!authenticated)
            throw new AppException(ErrorCodes.UNAUTHENTICATED);

        String token = tokenUtil.generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request)
            throws ParseException, JOSEException {
        String token = request.getToken();
        SignedJWT signedJWT = tokenService.verifyToken(request.getToken(), true);

        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        String tokenVersion = jwtUtil.getTokenVersion(token);
        long adjustedExpireTime = tokenUtil.aroundTimeToken(signedJWT);

        redisTemplate.opsForValue().set(jit,tokenVersion, adjustedExpireTime, TimeUnit.SECONDS);

        String email = signedJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findByEmail((email))
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        String newToken = tokenUtil.generateToken(user);

        return AuthenticationResponse.builder()
                .token(newToken)
                .authenticated(true)
                .build();
    }

    public void logout(LogoutRequest request) {
        String token = request.getToken();
        try {
            SignedJWT signedJWT = tokenService.verifyToken(token, true);
            String tokenKey = signedJWT.getJWTClaimsSet().getJWTID();
            String tokenVersion = jwtUtil.getTokenVersion(token);
            long adjustedExpireTime = tokenUtil.aroundTimeToken(signedJWT);

            redisTemplate.opsForValue().set(tokenKey,tokenVersion, adjustedExpireTime, TimeUnit.SECONDS);
            AuthenticationResponse.builder()
                    .build();
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCodes.UNAUTHENTICATED);
        }
    }

    @Transactional
    public void resetPassword(ChangePasswordRequest request, HttpServletRequest req) throws ParseException {

        String token = securityUtil.getTokenFromRequest(req);

        String email = securityUtil.extractEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCodes.UNAUTHENTICATED));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisUtil.save(token, String.valueOf(user.getTokenVersion()));
    }



}
