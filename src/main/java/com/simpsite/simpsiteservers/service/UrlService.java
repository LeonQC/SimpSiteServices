package com.simpsite.simpsiteservers.service;

import com.simpsite.simpsiteservers.Codec.Codec;
import com.simpsite.simpsiteservers.model.UrlData;
import com.simpsite.simpsiteservers.repository.UrlRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final UrlRepository urlRepository;

    private final RedisTemplate<String,UrlData> redisTemplate;
    private final CodecFactory codecFactory;


    @Cacheable(value = "shortUrlCache", key = "#longUrl",unless = "#result == null")
    public String shortenUrl(String longUrl) {
//        Codec codec = selectCodec(longUrl);
        UrlData urlData = new UrlData();
        Optional<UrlData> existingUrlData = urlRepository.findByLongUrl(longUrl);
        if(existingUrlData.isPresent()){
            return existingUrlData.get().getLongUrl();
        }
        Codec codec = codecFactory.createCodec(selectCodec(longUrl));

        String newShortUrl = codec.encode(longUrl) ;

        urlData.setLongUrl(longUrl);
        urlData.setShortUrl(newShortUrl);

        redisTemplate.opsForValue().set(newShortUrl,urlData);
        urlRepository.save(urlData);
        return newShortUrl;
    }

    public String selectCodec(String longUrl) {
        if (longUrl.startsWith("https")){
            return "Base62";
        } else if (longUrl.startsWith("http")) {
            return "Hash";
        } else {
            return "Random";
        }
    }


    public String getOriginalUrl(String shortUrl) {
        Optional<UrlData> url = urlRepository.findByShortUrl(shortUrl);
        if (url.isPresent()) {
            return url.get().getLongUrl();
        } else {
            // Try to get from Redis
            UrlData originalUrl = redisTemplate.opsForValue().get(shortUrl);
            if (originalUrl != null) {
                return originalUrl.getLongUrl();
            } else {
                throw new NoSuchElementException("No URL found for " + shortUrl);
            }
        }
    }



}
