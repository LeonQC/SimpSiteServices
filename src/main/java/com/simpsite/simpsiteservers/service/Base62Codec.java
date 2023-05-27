package com.simpsite.simpsiteservers.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Base62Codec implements Codec{
    private final Map<Integer,String> dataBase = new HashMap<>();
    private int id;

    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Override
    public String encode(String longUrl) {
        id++;
        dataBase.put(id,longUrl);
        String base62Encoded = base62Encoded(id);
        return "http://simpsite.com/"+base62Encoded;
    }

    private String base62Encoded(int number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0 ){
            int remainder = number % 62;
            char base62Char = toBase62Char(remainder);
            encoded.insert(0,CHARS.charAt(remainder));
            System.out.println(Thread.activeCount());
            System.out.println(Runtime.getRuntime().availableProcessors());
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
        if (digit >= 0 && digit <= 9) {
            return (char) (digit + '0');
        } else if (digit >= 10 && digit <= 35) {
            return (char) (digit - 10 + 'a');
        } else if (digit >= 36 && digit <= 61) {
            return (char) (digit - 36 + 'A');
        } else {
            throw new IllegalArgumentException("Invalid digit: " + digit);
        }
    }
//
//    public static long encode(String shortUrl){
//        long id = 0;
//        for(int i = 0; i < shortUrl.length(); i++) {
//            id = id * 62 + toBase62(shortUrl.charAt(i));
//        }
//        return id;
//    }
//
//    public static String generate(long id, int length){
//        String shortUrl = "";
//        while (id > 0) {
//            shortUrl = CHARS.charAt((int) id % 62) + shortUrl;
//            id /= 62;
//        }
//
//        while (shortUrl.length() < length) {
//            shortUrl = "0" + shortUrl;
//        }
//
//        return shortUrl;
//    }
}
