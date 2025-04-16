package com.project.dadn.utlls;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.dadn.dtos.requests.ChangePasswordRequest;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.models.User;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.services.TokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenUtil {

    private final JwtUtil jwtUtil;
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long validDuration;

    RedisTemplate<String, String> redisTemplate;



    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("dadn.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(validDuration, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new AppException(ErrorCodes.UNCATEGORIZED_ERROR);
        }
    }

    public String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

    public long aroundTimeToken(SignedJWT signedJWT) throws ParseException {

        long expireTime = signedJWT.getJWTClaimsSet().getExpirationTime().getTime() - System.currentTimeMillis();

        int randomOffset = ThreadLocalRandom.current().nextInt(-120, 120);
        return expireTime + (randomOffset * 1000L);
    }

    public void removeNearExpiryToken() {
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();

        Set<String> expiredTokens = zSetOperations.range("jwt_tokens", 0, 0);

        if (expiredTokens != null && !expiredTokens.isEmpty()) {
            String tokenToRemove = expiredTokens.iterator().next();
            zSetOperations.remove("jwt_tokens", tokenToRemove);
            log.info("Token has been removed from expired tokens: {}", expiredTokens);
        } else {
            log.info("Token is empty");
        }
    }

}
