package com.simpsite.simpsiteservers.security;

import com.simpsite.simpsiteservers.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public static String generateJwtToken(Authentication authentication, int expiration, SecretKey key){
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expiration))
                .signWith(key)
                .compact();
    }

    public String getUserNameFormJwtToken(String token,SecretKey key){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String token,SecretKey key){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token);
            return true;
        } catch (InvalidClaimException e) {
            logger.error("Incalid JWT signature:{}",e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token:{}",e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired:{}",e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported:{}",e.getMessage());
        } catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty:{}",e.getMessage());
        }
        return false;
    }
}
