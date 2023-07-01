package com.simpsite.simpsiteservers.Codec;

public interface Codec {
    String encode(String longUrl);
    String decode(String shortUrl);
}
