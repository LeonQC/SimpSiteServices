package com.simpsite.simpsiteservers.security;

import com.simpsite.simpsiteservers.Utils.CookieUtils;
import com.simpsite.simpsiteservers.config.WebSecurityJWTProperties;
import com.simpsite.simpsiteservers.service.UserDetailsImpl;
import com.simpsite.simpsiteservers.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private WebSecurityJWTProperties webSecurityJWTProperties;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String jwt = parseJwtCookie(request);
            if(jwt == null){
                jwt = parseJwt(request);
            }
            if (jwt != null && jwtUtils.validateJwtToken(jwt,webSecurityJWTProperties.getSecretKey())){
                String username = jwtUtils.getUserNameFormJwtToken(jwt, webSecurityJWTProperties.getSecretKey());
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,null,userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }catch (Exception e){
            logger.error("Cannot set user authentication:{}" ,e);
        }
        filterChain.doFilter(request,response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer")){
            return headerAuth.substring(7,headerAuth.length());
        }
        return null;
    }

    private String parseJwtCookie(HttpServletRequest request) {
        Optional<Cookie> cookieOptional = CookieUtils.getCookie(
                request,
                webSecurityJWTProperties.getAccessTokenCookieName()
        );
        return cookieOptional.map(Cookie::getValue).orElse(null);
    }
}
