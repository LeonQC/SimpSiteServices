package com.simpsite.simpsiteservers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.RedisScript;
@Configuration
public class RateScriptConfig {
        @Bean
        public RedisScript<Long> limiterScript() {
            Resource scriptSource = new ClassPathResource("redisLimiter.lua");
            return RedisScript.of(scriptSource, Long.class);
        }

}
