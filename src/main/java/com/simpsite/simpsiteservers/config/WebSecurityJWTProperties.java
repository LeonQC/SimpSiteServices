package com.simpsite.simpsiteservers.config;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@ConfigurationProperties(prefix = "simpsite.security.jwt")
@Data
public class WebSecurityJWTProperties {
    private String secret;
    private Integer tokenExpiration;
    private String accessTokenCookieName;
    private String refreshTokenCookieName;
    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
