package com.simpsite.simpsiteservers.Codec;

import com.simpsite.simpsiteservers.service.Counter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Base62Codec implements Codec {
    private final Map<Long,String> dataBase = new HashMap<>();

    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final Counter counter;


    @Override
    public String encode(String longUrl) {
        Long id = counter.increment("global:counter");
        dataBase.put(id,longUrl);
        String base62Encoded = base62Encoded(id);
        return "http://simpsite.com/"+base62Encoded;
    }

    private String base62Encoded(Long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0 ){
            int remainder =(int) ( number % 62);
            char base62Char = toBase62Char(remainder);
            encoded.insert(0,base62Char);
            System.out.println(base62Char);
            number = number /62;
        }
        while (encoded.length()<6){
            encoded.insert(0,"0");
        }
        return encoded.toString();
    }

    @Override
    public String decode(String shortUrl){
        int p = shortUrl.lastIndexOf('/')+1;
        int key = Integer.parseInt(shortUrl.substring(p));
        return dataBase.get(key);
    }

    private char toBase62Char(int digit) {
        return CHARS.charAt(digit);
    }

}
