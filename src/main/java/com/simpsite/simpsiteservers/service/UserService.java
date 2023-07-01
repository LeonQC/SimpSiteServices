package com.simpsite.simpsiteservers.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    UserDetails loadUserbyEmail(String email) throws UsernameNotFoundException;

}
