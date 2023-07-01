package com.simpsite.simpsiteservers.Codec;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class RandomCodec implements Codec{
    private final Map<String,String> dataBase = new HashMap<>();
    private Random random = new Random();

    @Override
    public String encode (String longUrl){
        String base62Encoded = generateBase62Encoded();
        String randomChars = generateRandomChars(6 - base62Encoded.length());
        String shortUrl = randomChars + base62Encoded;

        dataBase.put(shortUrl, longUrl);
        return "http://simpsite.com/" + shortUrl;
    }

    private String generateBase62Encoded() {
        int number = random.nextInt(Integer.MAX_VALUE);
        return base62Encode(number);
    }

    private String generateRandomChars(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    private String base62Encode(int number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = number % 62;
            encoded.insert(0, CHARS.charAt(remainder));
            number = number / 62;
        }
        return encoded.toString();
    }
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    @Override
    public String decode(String shortUrl) {
        int p = shortUrl.lastIndexOf('/')+1;
        System.out.println(p);
        int key = Integer.parseInt(shortUrl.substring(p));
        System.out.println(key);

        return dataBase.get(key);
    }

//    public static String generate(int length) {
//        Random random = new Random();
//        StringBuilder sb = new StringBuilder();
//
//        while (sb.length() < length) {
//            int index = random.nextInt(CHARS.length());
//            sb.append(CHARS.charAt(index));
//        }
//
//        return sb.toString();
//    }

}
//    @Bean
//    public RedisScript<Long> limiterScript() {
//        Resource scriptSource = new ClassPathResource("redisLimiterCounter.lua");
//        return RedisScript.of(scriptSource, Long.class);
//    }
//public long getNextSequenceIdByLua() {
//    long sequenceId = this.sequenceIdRedisTemplate.execute(redisCounterScript, List.of(GLOBAL_SEQUENCE_ID));
//    sequenceIdRedisTemplate.getConnectionFactory().getConnection().bgSave();
//    return sequenceId;
//}