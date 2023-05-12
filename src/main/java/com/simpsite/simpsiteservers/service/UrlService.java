package com.simpsite.simpsiteservers.service;

import com.simpsite.simpsiteservers.model.UrlData;
import com.simpsite.simpsiteservers.repository.LongUrlRepository;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
public class UrlService {
    private final LongUrlRepository longUrlRepository;

    private final RedisTemplate<String,UrlData> redisTemplate;

    private final Codec codec;

    public UrlService(LongUrlRepository longUrlRepository, RedisTemplate<String, UrlData> redisTemplate, @Qualifier("base62Codec") Codec codec) {
        this.longUrlRepository = longUrlRepository;
        this.redisTemplate = redisTemplate;
        this.codec = codec;
    }


    @Cacheable(value = "shortUrlCache", key = "#longUrl",unless = "#result == null")
    public String shortenUrl(String longUrl) {
        String newShortUrl = codec.encode(longUrl);
        UrlData urlData = new UrlData();
        urlData.setLongUrl(longUrl);
        urlData.setShortUrl(newShortUrl);
        longUrlRepository.save(urlData);
        return newShortUrl;
    }


}
