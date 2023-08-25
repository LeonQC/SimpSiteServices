package com.simpsite.simpsiteservers.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import org.springframework.data.redis.serializer.StringRedisSerializer;



@Configuration
@EnableConfigurationProperties({RedisDBProperties.class})
@EnableRedisRepositories
public class RedisDBConfig {

    @Bean(
            name = {"redisConnectionFactory"}
    )
    public LettuceConnectionFactory redisConnectionFactory(RedisDBProperties redisDBProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisDBProperties.getHost());
        redisStandaloneConfiguration.setPort(redisDBProperties.getPort());
        redisStandaloneConfiguration.setPassword(redisDBProperties.getPassword());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean(
            name = {"sequenceIdTemplate"}
    )
    public RedisTemplate<String, Long> sequenceIDRedisTemplate(@Qualifier("redisConnectionFactory") LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer(Long.class));
        return template;
    }
}