package com.simpsite.simpsiteservers.controller;

import com.simpsite.simpsiteservers.service.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/encode")
    public ResponseEntity<String> encode(@RequestBody String longUrl)  {
//        String shortUrl = codecImpl1.encode(longUrl);
        String shortUrl = urlService.shortenUrl(longUrl);

        // Return the short URL in the response
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/decode")
    public ResponseEntity<String> decode(@RequestParam("shortUrl") String shortUrl) {

        String longUrl = base62Codec.encode(shortUrl);

        return ResponseEntity.ok(longUrl);
    }
}

