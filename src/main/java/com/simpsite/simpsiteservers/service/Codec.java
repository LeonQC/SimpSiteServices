package com.simpsite.simpsiteservers.service;

public interface Codec {
    String encode(String longUrl);
    String decode(String shortUrl);
}
