package com.simpsite.simpsiteservers.controller;

import com.simpsite.simpsiteservers.Codec.Codec;
import com.simpsite.simpsiteservers.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UrlController {
    private final Codec base62Codec;

    private final Codec hashCodec;

    private final Codec randomCodec;

    private final UrlService urlService;


    public UrlController(Codec base62Codec, Codec hashCodec, Codec randomCodec, UrlService urlService) {
        this.base62Codec = base62Codec;
        this.hashCodec = hashCodec;
        this.randomCodec = randomCodec;
        this.urlService = urlService;
    }

    @PostMapping("/encode")
    public ResponseEntity<String> encode(@RequestBody String longUrl)  {
//        String shortUrl = codecImpl1.encode(longUrl);
        String shortUrl = urlService.shortenUrl(longUrl);

        // Return the short URL in the response
        return ResponseEntity.ok(shortUrl);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortUrl){
        urlService.deleteUrl(shortUrl);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/decode")
    public ResponseEntity<String> decode(@RequestParam("shortUrl") String shortUrl) {

        String longUrl = urlService.getOriginalUrl(shortUrl);

        return ResponseEntity.ok(longUrl);
    }



}

