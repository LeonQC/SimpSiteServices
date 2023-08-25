package com.simpsite.simpsiteservers.config;

import com.simpsite.simpsiteservers.constants.SecurityConstants;
import com.simpsite.simpsiteservers.model.Customer;
import com.simpsite.simpsiteservers.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            ;
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String email=oAuth2User.getAttribute("html_url");
            String username = oAuth2User.getAttribute("login");
            if(!userRepository.existsByEmail(email)){
                Customer customer =new Customer();
                customer.setEmail(email);
                customer.setUsername(username);
                customer.setPassword(passwordEncoder.encode(email));
                userRepository.save(customer);
            }
            String userCookieValue = Base64.getEncoder().encodeToString(oAuth2User.toString().getBytes());
            Cookie cookie = new Cookie("user",userCookieValue);
            cookie.setHttpOnly(false);
            cookie.setMaxAge(60*60);
            response.addCookie(cookie);
            // 重定向用户回前端应用
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000");

    }
}}
