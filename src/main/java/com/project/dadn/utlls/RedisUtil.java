package com.project.dadn.utlls;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long validDuration;

    public void save(String token, String version) {
        redisTemplate.opsForValue().set(token, version, validDuration, TimeUnit.SECONDS);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
