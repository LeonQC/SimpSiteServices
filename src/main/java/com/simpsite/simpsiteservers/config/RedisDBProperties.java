package com.simpsite.simpsiteservers.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "redis")
public class RedisDBProperties {

    private String host;
    private int port;
    private String password;

}
