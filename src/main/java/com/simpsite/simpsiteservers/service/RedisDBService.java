package com.simpsite.simpsiteservers.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisDBService {

    private static final String SEQUENCE_ID = "Sequence_ID";

    private RedisAtomicLong redisAtomicLong;

    private final RedisTemplate<String,Long> redisTemplate;

    @PostConstruct
    public void setUp(){
        redisAtomicLong = new RedisAtomicLong(SEQUENCE_ID,redisTemplate);
    }

    public long getNextSequenceIdByAtomic(){
        long increment = redisAtomicLong.getAndIncrement();
        redisTemplate.getConnectionFactory().getConnection().bgSave();
        return increment;
    }

}
