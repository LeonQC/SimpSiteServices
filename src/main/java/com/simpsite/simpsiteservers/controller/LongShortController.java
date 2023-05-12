package com.simpsite.simpsiteservers.controller;

import com.simpsite.simpsiteservers.service.LongUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;


@RestController

public class LongShortController {
    @Autowired
    private LongUrlService longurlService;
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/shorten")
    public String shorten(@RequestBody String longUrl) throws NoSuchAlgorithmException {
        return longurlService.shortenUrl(longUrl);
    }
}

