package com.simpsite.simpsiteservers.config;

import com.simpsite.simpsiteservers.Filter.*;

import com.simpsite.simpsiteservers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
//        http.securityContext((context)->context.requireExplicitSave(false))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                        CorsConfiguration config = new CorsConfiguration();
//                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//                        config.setAllowedMethods(Collections.singletonList("*"));
//                        config.setAllowCredentials(true);
//                        config.setAllowedHeaders(Collections.singletonList("*"));
////                        config.setExposedHeaders(Arrays.asList("Authorization"));
//                        config.setMaxAge(3600L);
//                        return config;
//                    }
//                })).csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers( "/encode","/register")
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
//                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
//                .authorizeHttpRequests()
//                        .requestMatchers("/encode","/register","/users").permitAll()
//                .anyRequest().authenticated()
//                .and().formLogin().loginPage("/login").successHandler(successHandler)
//                .and().oauth2Login().loginPage("/login").successHandler(successHandler);

        http
//                .securityContext((context) -> context.requireExplicitSave(false))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer -> corsCustomizer
                        .configurationSource(corsConfigurationSource())
                )



                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthLoggingAfterFilter(),BasicAuthenticationFilter.class)
                .addFilterBefore(new TokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests()
                .requestMatchers(new AntPathRequestMatcher("/encode"), new AntPathRequestMatcher("/register")).permitAll()
                .anyRequest().authenticated()
                .and().httpBasic().and()
                .oauth2Login()
                .successHandler(new CustomSuccessHandler(userRepository,passwordEncoder));




        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
