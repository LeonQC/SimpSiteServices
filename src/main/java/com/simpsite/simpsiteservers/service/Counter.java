package com.simpsite.simpsiteservers.service;

import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class Counter {
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script = new DefaultRedisScript<>();

    @PostConstruct
    private void init() {
        script.setLocation(new ClassPathResource("scripts/increment.lua"));
        script.setResultType(Long.class);
    }

    public Long increment(String key) {
        return redisTemplate.execute(script, Collections.singletonList(key));
    }
}
