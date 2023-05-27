package com.simpsite.simpsiteservers.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HashCodec implements Codec{
    static final int K1 = 1117;
    static final int K2 = 1000000007;

    private final Map<Integer,String> dataBase = new HashMap<>();

    private final Map<String,Integer> urlToKey = new HashMap<>();

    @Override
    public String encode(String longUrl){
        if( urlToKey.containsKey(longUrl)){
            return "http://simpsite.com/" + urlToKey.get(longUrl);
        }
        int key = 0;
        long base = 1;
        for(int i = 0; i < longUrl.length();i++){
            char c = longUrl.charAt(i);
            key = (int) ((key + (long)c * base) % K2);
            base = (base * K1) % K2;
        }
        while (dataBase.containsKey(key)){
            key = (key + 1) % K2;
        }
        dataBase.put(key,longUrl);
        urlToKey.put(longUrl,key);

        return "http://simpsite.com/" + key;
    }

    @Override
    public String decode(String shortUrl){
        int p = shortUrl.lastIndexOf('/') + 1;
        int key = Integer.parseInt(shortUrl.substring(p));
        return dataBase.get(key);
    }
}
