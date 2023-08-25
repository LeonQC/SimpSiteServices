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
        Long result = redisTemplate.execute(script, Collections.singletonList(key));
        persistCounter(key,result);
        return result;
    }

    public Long getCounterValue(String key) {
        String counterValue = redisTemplate.opsForValue().get(key);
        if (counterValue != null) {
            return Long.parseLong(counterValue);
        }
        return null;
    }

    public void setCounterValue(String key,Long value) {
        redisTemplate.opsForValue().set(key, value.toString());
    }

    public void persistCounter(String key, Long counter){
        redisTemplate.opsForValue().set(key,counter.toString());
    }

}

