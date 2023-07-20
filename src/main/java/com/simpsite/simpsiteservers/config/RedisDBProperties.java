package com.simpsite.simpsiteservers.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
@Data
public class RedisDBProperties {
    private String host;
    private Integer port;
    private String password;
}