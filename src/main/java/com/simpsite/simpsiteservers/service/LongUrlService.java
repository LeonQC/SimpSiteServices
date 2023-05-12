package com.simpsite.simpsiteservers.service;

import com.simpsite.simpsiteservers.model.Long2ShortUrl;
import com.simpsite.simpsiteservers.repository.LongUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class LongUrlService {
    @Autowired
    private LongUrlRepository longUrlRepository;

    @Autowired
    private RedisTemplate<String, Long2ShortUrl> redisTemplate;

    public String shortenUrl(String longUrl) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(longUrl.getBytes(StandardCharsets.UTF_8));
        String shortUrl = new BigInteger(1, hash).toString(16).substring(0, 8);  // use first 8 characters as short url

        Long2ShortUrl long2ShortUrl = redisTemplate.opsForValue().get(shortUrl);
        if (long2ShortUrl == null) {
            long2ShortUrl = longUrlRepository.findById(shortUrl).orElse(null);
            if (long2ShortUrl == null) {
                long2ShortUrl = new Long2ShortUrl();
                long2ShortUrl.setShortUrl(shortUrl);
                long2ShortUrl.setLongUrl(longUrl);
                longUrlRepository.save(long2ShortUrl);
            }
            redisTemplate.opsForValue().set(shortUrl, long2ShortUrl);
        }

        return long2ShortUrl.getShortUrl();
    }


}
