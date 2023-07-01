package com.simpsite.simpsiteservers.service;

import com.simpsite.simpsiteservers.model.Customer;
import com.simpsite.simpsiteservers.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("html_url");
        System.out.println(email);
        List<Customer> optionalCustomer = userRepository.findByEmail(email);

        return oAuth2User;
    }

private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
    String userEmail = oAuth2User.getAttribute("email"); // assuming 'email' attribute contains user's email

    if(StringUtils.isEmpty(userEmail)) {
        throw new IllegalArgumentException("Email not found from OAuth2 provider");
    }

    List<Customer> customers = userRepository.findByEmail(userEmail);
    Customer customer;
    if(!customers.isEmpty()) {
        customer = customers.get(0);
        customer = updateExistingUser(customer, oAuth2User);
    } else {
        customer = registerNewUser(oAuth2UserRequest, oAuth2User);
    }

    // Here we are just returning the OAuth2User object.
    // If you want to add extra details to this object, you might need to create a new class that extends OAuth2User.
    return oAuth2User;
}


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<Customer> customer = userRepository.findByEmail(email);
        if(customer.isEmpty()){
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new User(customer.get(0).getEmail(),customer.get(0).getPwd(), Collections.emptyList());
    }

    private Customer registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        Customer customer = new Customer();

        String hashPwd = passwordEncoder.encode(customer.getPwd());
        String username = oAuth2User.getAttribute("login");
        customer.setPwd(hashPwd);

        return userRepository.save(customer);
    }

    private Customer updateExistingUser(Customer existingUser,  OAuth2User oAuth2User) {
        existingUser.setEmail(oAuth2User.getAttribute("login"));

        return userRepository.save(existingUser);
    }
}
