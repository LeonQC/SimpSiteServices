package com.simpsite.simpsiteservers.service;

import com.simpsite.simpsiteservers.model.Customer;
import com.simpsite.simpsiteservers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserbyEmail(String email) throws UsernameNotFoundException {
        if(userRepository.existsByEmail(email)){
            Customer customer = userRepository.findByEmail(email).get(0);
        }else{
            throw new UsernameNotFoundException("User not found with email" + email);
        }
        return null;
    }
}
