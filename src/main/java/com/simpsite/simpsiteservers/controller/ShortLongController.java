package com.simpsite.simpsiteservers.controller;

import com.simpsite.simpsiteservers.model.Long2ShortUrl;
import com.simpsite.simpsiteservers.repository.LongUrlRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class ShortLongController {

    private final RedisTemplate<String, Long2ShortUrl> redisTemplate;

    private final LongUrlRepository longUrlRepository;
    public ShortLongController(RedisTemplate<String, Long2ShortUrl> redisTemplate, LongUrlRepository longUrlRepository) {
        this.redisTemplate = redisTemplate;
        this.longUrlRepository = longUrlRepository;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> getLongUrl(@PathVariable String shortUrl){
        Long2ShortUrl long2ShortUrl = redisTemplate.opsForValue().get(shortUrl);
        if(long2ShortUrl == null){
            long2ShortUrl = longUrlRepository.findById(shortUrl).orElse(null);
            if(long2ShortUrl == null){
                return ResponseEntity.notFound().build();
            }
            redisTemplate.opsForValue().set(shortUrl,long2ShortUrl);
        }
        return ResponseEntity.ok(long2ShortUrl.getLongUrl());
    }




}
