package com.simpsite.simpsiteservers.security;

import com.simpsite.simpsiteservers.Utils.CookieUtils;
import com.simpsite.simpsiteservers.config.WebSecurityJWTProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.simpsite.simpsiteservers.security.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final int cookieExpireSeconds = 1800;

    private final List<String> AUTHORIZED_URI = Arrays.asList(
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3000/login",
            "http://localhost:3000/home",
            "http://localhost:3000/oauth2/redirect"
    );
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    private WebSecurityJWTProperties webSecurityJWTProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        log.info("oauth2 handler");
        String targetUrL = determineTargetUrl(request,response,authentication);
        if(response.isCommitted()) {
            logger.info("Response has already been committed. Unable to redirect to " + targetUrL);
            return;
        }
        clearAuthenticationAttributes(request,response);
        String token = JwtUtils.generateJwtToken(
                authentication,
                webSecurityJWTProperties.getTokenExpiration(),
                webSecurityJWTProperties.getSecretKey());
        log.info("cookie:" + URLEncoder.encode(token, StandardCharsets.UTF_8));
        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtils.createCookieString(
                webSecurityJWTProperties.getAccessTokenCookieName(),
                token,
                cookieExpireSeconds
        ));
        getRedirectStrategy().sendRedirect(request,response,targetUrL);
    }

    protected String determineTargetUri(HttpServletRequest request,HttpServletResponse response,Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request,REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        if (redirectUri.isPresent() && !isAlwaysUseDefaultTargetUrl(redirectUri.get())){
            throw new RuntimeException("Sorry! We've got an Unauthorized URI and can't proceed with authentication");
        }
        log.info(getDefaultTargetUrl());
        return redirectUri.orElse(getDefaultTargetUrl());
    }

    private boolean isAlwaysUseDefaultTargetUrl(String s) {
        URI clientRedirectUri = URI.create(s);
        for(String uriString :AUTHORIZED_URI){
            URI authorizedUri = URI.create(uriString);
            if(authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedUri.getPort() == clientRedirectUri.getPort()){
                return true;
            }
        }
        return false;
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request,response);
    }
}
