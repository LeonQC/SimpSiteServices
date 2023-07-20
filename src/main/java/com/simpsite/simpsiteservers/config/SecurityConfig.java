package com.simpsite.simpsiteservers.config;

import com.simpsite.simpsiteservers.Filter.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
private final UsernamePwdAuthProvider usernamePwdAuthProvider;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

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
//                }))

//                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
//                .authorizeHttpRequests()
//                        .requestMatchers("/encode","/register","/users").permitAll()
//                .anyRequest().authenticated()
//                .and().formLogin().loginPage("/login").successHandler(successHandler)
//                .and().oauth2Login().loginPage("/login").successHandler(successHandler);

        http
                .csrf((csrf) -> csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers( "/encode","/register")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .securityContext((context) -> context.requireExplicitSave(false))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer -> corsCustomizer
                        .configurationSource(corsConfigurationSource())
                )





                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
//                .addFilterAfter(new AuthLoggingAfterFilter(),BasicAuthenticationFilter.class)

                .authorizeHttpRequests()
                .requestMatchers("/encode","/register","/delete","/test/**").permitAll()
                .requestMatchers("/deletea").authenticated()

                .anyRequest().authenticated()
                .and().formLogin().and().httpBasic()
                .and()
//                .oauth2ResourceServer(oauth2ResourceServerCustomizer ->
//                        oauth2ResourceServerCustomizer.jwt(jwtCustomizer-> jwtCustomizer.jwtAuthenticationConverter(jwtAuthenticationConverter)));
                .oauth2Login();
//                .successHandler(new CustomSuccessHandler(userRepository,passwordEncoder));
        return http.build();
    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(usernamePwdAuthProvider);
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
