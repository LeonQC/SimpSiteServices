package com.simpsite.simpsiteservers.ratelimit;

public interface RateLimiter {
    boolean isRateLimited (String key, RateVariable rateVariable );
}
