package com.project.dadn.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.project.dadn.dtos.requests.ChangePasswordRequest;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.models.User;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.utlls.JwtUtil;
import com.project.dadn.utlls.RedisUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    private final RedisUtil redisUtil;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    String tokenVersionString = "token_version";
    UserRepository userRepository;
    JwtUtil jwtUtil;
    PasswordEncoder passwordEncoder;


    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        if (!signedJWT.verify(verifier) || signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date())) {
            throw new AppException(ErrorCodes.UNAUTHENTICATED);
        }

        String tokenKey = signedJWT.getJWTClaimsSet().getJWTID();
        String email = signedJWT.getJWTClaimsSet().getSubject();
        int tokenVersion = signedJWT.getJWTClaimsSet().getIntegerClaim(tokenVersionString);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCodes.UNAUTHENTICATED));

        if (isRefresh && user.getTokenVersion() < tokenVersion) {
            throw new AppException(ErrorCodes.UNAUTHENTICATED);
        }

        redisUtil.save(tokenKey, String.valueOf(user.getTokenVersion()));

        return signedJWT;
    }

    @Async
    @Transactional(readOnly = true)
    public CompletableFuture<Boolean> checkTokenVersion(String token) throws ParseException {
        String version = jwtUtil.getTokenVersion(token);
        String email = jwtUtil.getEmailToken(token);

        if (version == null || version.isEmpty()) {
            return CompletableFuture.completedFuture(false);
        }

        return userRepository.findByEmail(email)
                .map(user -> CompletableFuture.completedFuture(version.equals(String.valueOf(user.getTokenVersion()))))
                .orElse(CompletableFuture.completedFuture(false));
    }

    public void validateTokenVersion(String token) throws ParseException {
        String version = jwtUtil.getTokenVersion(token);
        boolean isValidVersion = checkTokenVersion(token).join();

        if (!isValidVersion) {
            throw new AppException(ErrorCodes.UNAUTHENTICATED);
        }

        redisUtil.save(token, version);
    }

    public void saveResetPassword(String email, ChangePasswordRequest request) throws ParseException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

}
