package com.simpsite.simpsiteservers.security;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.simpsite.simpsiteservers.Utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_requests";

    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

    public static final int cookieExpireSeconds = 1800;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        try{
            OAuth2AuthorizationRequest c = CookieUtils.getCookie(request,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                    .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                    .orElse(null);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        OAuth2AuthorizationRequest c = CookieUtils.getCookie(request,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
        log.info(c.getRedirectUri());
        return c;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("saved");
        if(authorizationRequest == null) {
            CookieUtils.deleteCookie(
                    request,
                    response,
                    OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME
            );
            CookieUtils.deleteCookie(
                    request,
                    response,
                    REDIRECT_URI_PARAM_COOKIE_NAME
            );
            return;
        }
        CookieUtils.addCookie(
                response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtils.serialize(authorizationRequest),
                cookieExpireSeconds
        );

        String redirectUriAfterLogin = request.getParameter(
                REDIRECT_URI_PARAM_COOKIE_NAME
        );

        if (StringUtils.isNotBlank(redirectUriAfterLogin)){
            CookieUtils.addCookie(
                    response,
                    REDIRECT_URI_PARAM_COOKIE_NAME,
                    redirectUriAfterLogin,
                    cookieExpireSeconds
            );
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        log.info("remove auth request old version");
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request,HttpServletResponse response){
        CookieUtils.deleteCookie(
                request,
                response,
                OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME
        );
        CookieUtils.deleteCookie(
                request,
                response,
                REDIRECT_URI_PARAM_COOKIE_NAME
        );
    }
}
