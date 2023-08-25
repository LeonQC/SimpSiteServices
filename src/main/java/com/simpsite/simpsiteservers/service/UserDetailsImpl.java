package com.simpsite.simpsiteservers.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simpsite.simpsiteservers.constants.AuthProvider;
import com.simpsite.simpsiteservers.model.Customer;
import com.simpsite.simpsiteservers.repository.UserRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
public class UserDetailsImpl implements UserDetails, OidcUser {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String accountName;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private AuthProvider authProvider;

    private OidcUser oidcUser;
    public UserDetailsImpl(Long id, String accountName, String email, String password,
                           Collection<? extends GrantedAuthority> authorities, AuthProvider authProvider) {
        this.id = id;
        this.accountName = accountName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.authProvider = authProvider;
    }
    public static UserDetailsImpl build(Customer user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getProvider());
    }

    public static UserDetailsImpl create(Customer user, OidcUser oidcUser) {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        userDetails.setOidcUser(oidcUser);
        return userDetails;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return getClaims();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return accountName;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}