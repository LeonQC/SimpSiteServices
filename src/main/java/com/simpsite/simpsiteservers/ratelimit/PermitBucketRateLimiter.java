package com.simpsite.simpsiteservers.ratelimit;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
@Component
public class PermitBucketRateLimiter implements RateLimiter {
    private final RedisTemplate<String, Serializable> limitRedisTemplate;

    public PermitBucketRateLimiter(RedisTemplate<String, Serializable> limitRedisTemplate) {
        this.limitRedisTemplate = limitRedisTemplate;
    }

    @Autowired
    @Qualifier("limiterScript")
    private RedisScript<Long> redisLimiterScript;

    @Override
    public boolean isRateLimited(String key, RateVariable rateVariable) {
        List<String> keys = getKeys(key);
        double permitsPerSecond = rateVariable.getPermitsPerSecond();
        int capacity = rateVariable.getPermits();

        int now = Integer.parseInt(String.valueOf(getCurrentTimeStamp()));

        Number count = limitRedisTemplate.execute(
                redisLimiterScript,
                keys,
                permitsPerSecond,
                capacity,
                now,
                1);

        return count.intValue() != 1;
    }

    private long getCurrentTimeStamp() {
        Instant instant = Instant.now();
        return instant.getEpochSecond();
    }

    private List<String> getKeys(String key) {
        String tokenKey = "rate_limiter.{" + key + "}.tokens";
        String timestampKey = "rate_limiter.{" + key + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }
}
