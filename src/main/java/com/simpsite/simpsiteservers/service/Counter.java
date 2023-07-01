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

//    public long getNextSequenceIdByAtomic() {
//        long increment = entityIdCounter.getAndIncrement();
//        sequenceIDRedisTemplate.getConnectionFactory().getConnection().bgSave();
//        return increment;
//    }
//
//    import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.script.RedisScript;
//import org.springframework.data.redis.support.atomic.RedisAtomicLong;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//    @Service
//    public class SequenceIdService {
//        private static final String SEQUENCE_ID = "Sequence_ID";
//        private static final String GLOBAL_SEQUENCE_ID = "Global_Sequence_ID";
//
//
//
//        private RedisAtomicLong entityIdCounter;
//        private final RedisTemplate<String, Long> sequenceIDRedisTemplate;
//        @Autowired
//        public SequenceIdService(@Qualifier("sequenceIdTemplate") RedisTemplate<String, Long> sequenceIDRedisTemplate) {
//            this.sequenceIDRedisTemplate = sequenceIDRedisTemplate;
//        }
//
//        @PostConstruct
//        public void setUp() {
//            entityIdCounter = new RedisAtomicLong(SEQUENCE_ID, sequenceIDRedisTemplate.getConnectionFactory());
//        }
//        public long getNextSequenceIdByAtomic() {
//            long increment = entityIdCounter.getAndIncrement();
//            sequenceIDRedisTemplate.getConnectionFactory().getConnection().bgSave();
//            return increment;
//        }
//    }
//@Configuration
//@EnableConfigurationProperties({RedisDBProperties.class})
//@EnableRedisRepositories
//public class RedisDBConfig {
//
//    @Bean(
//            name = {"redisConnectionFactory"}
//    )
//    public LettuceConnectionFactory redisConnectionFactory(RedisDBProperties redisDBProperties) {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(redisDBProperties.getHost());
//        redisStandaloneConfiguration.setPort(redisDBProperties.getPort());
//        redisStandaloneConfiguration.setPassword(redisDBProperties.getPassword());
//        return new LettuceConnectionFactory(redisStandaloneConfiguration);
//    }
//    @Bean(
//            name = {"sequenceIdTemplate"}
//    )
//    public RedisTemplate<String, Long> sequenceIDRedisTemplate(@Qualifier("redisConnectionFactory") LettuceConnectionFactory lettuceConnectionFactory) {
//        RedisTemplate<String, Long> template = new RedisTemplate();
//        template.setConnectionFactory(lettuceConnectionFactory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new Jackson2JsonRedisSerializer(Long.class));
//        return template;
//    }
//}
}

