package com.simpsite.simpsiteservers.ratelimit;

import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RateLimiterExecutor {
    @Getter
    private final PermitBucketRateLimiter permitBucketRateLimiter;

    public RateLimiterExecutor(PermitBucketRateLimiter permitBucketRateLimiter) {
        this.permitBucketRateLimiter = permitBucketRateLimiter;
    }


    public static boolean isRateLimited(RateLimiter rateLimiter, int period, int permits) {
        boolean tryAcquire = rateLimiter.tryAcquire(permits, (long) period, TimeUnit.SECONDS);
        return !tryAcquire;
    }
}