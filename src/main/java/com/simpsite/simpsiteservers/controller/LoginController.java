package com.simpsite.simpsiteservers.controller;

import com.simpsite.simpsiteservers.model.Customer;
import com.simpsite.simpsiteservers.repository.UserRepository;
import com.simpsite.simpsiteservers.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2UserService oAuth2UserService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Customer customer) {
        Customer savedCustomer = null;
        ResponseEntity response = null;
        try {
            if(userRepository.existsByEmail(customer.getEmail())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
            } else {
                String hashPwd = passwordEncoder.encode(customer.getPwd());
                customer.setPwd(hashPwd);
                userRepository.save(customer);
                return ResponseEntity.status(HttpStatus.CREATED).body("Given user details are successfully registered");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An exception occurred due to " + e.getMessage());
        }

    }

    @RequestMapping("/user")
    public Customer getUserDetailsAfterLogin(Authentication authentication) {
        List<Customer> customers = userRepository.findByEmail(authentication.getName());
        System.out.println(authentication.getName());
        System.out.println("customers"+customers);
        if (customers.size() > 0) {
            return customers.get(0);
        } else {
            return null;
        }

    }

//    @GetMapping("/oauth2/authorization/github")
//    public ResponseEntity<?> getUserDetails(Authentication authentication){
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        if(securityContext.getAuthentication().getPrincipal() instanceof DefaultOAuth2User) {
//            DefaultOAuth2User user = (DefaultOAuth2User) securityContext.getAuthentication().getPrincipal();
//            String userDetails = user.getAttribute("name") != null ? user.getName() :user.getAttribute("login");
//            return ResponseEntity.ok(userDetails);
//        } else{
//            List<Customer> customers =userRepository.findByEmail(authentication.getName());
//            if(!customers.isEmpty()){
//                Customer customer = customers.get(0);
//                return ResponseEntity.ok(customer.getEmail());
//            } else {
//                return ResponseEntity.ok("No User found.");
//            }
//        }
//    }



    @GetMapping("/")
    public Object currentUser(OAuth2AuthenticationToken token){

        return token.getPrincipal();
    }
    @GetMapping("/aaa")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {

        return Collections.singletonMap("user", principal.getAttribute("html_url"));
    }

    @GetMapping("/userinfo")
    public OAuth2User userinfo(@AuthenticationPrincipal OAuth2User oauth2User) {
        return oauth2User;
    }
    @PostMapping("/logins")
    public void loginUser(Customer customer){
        System.out.println("User" + customer);
        oAuth2UserService.loadUserByUsername(customer.getEmail());
    }

    @GetMapping("/profile")
    public Customer getCurrentUser(){
        String email = getCurrentUser().getEmail();
        return userRepository.findByEmail(email).get(0);
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
