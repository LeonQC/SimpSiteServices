package com.simpsite.simpsiteservers.controller;

import com.simpsite.simpsiteservers.Utils.CookieUtils;
import com.simpsite.simpsiteservers.config.WebSecurityJWTProperties;
import com.simpsite.simpsiteservers.constants.RoleName;
import com.simpsite.simpsiteservers.model.Customer;
import com.simpsite.simpsiteservers.model.Role;
import com.simpsite.simpsiteservers.model.UrlData;
import com.simpsite.simpsiteservers.repository.RoleRepository;
import com.simpsite.simpsiteservers.repository.UrlRepository;
import com.simpsite.simpsiteservers.repository.UserRepository;
import com.simpsite.simpsiteservers.security.*;
import com.simpsite.simpsiteservers.service.OAuth2UserService;
import com.simpsite.simpsiteservers.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "*" )
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {
    private static final int cookieExpireSeconds = 180;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    WebSecurityJWTProperties jwtProperties;

    @Autowired
    PasswordEncoder encoder;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UrlRepository urlRepository;
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
                String hashPwd = passwordEncoder.encode(customer.getPassword());
                customer.setPassword(hashPwd);
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

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        log.info(loginRequest.getUsername());
        log.info("login: " + loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        if(authentication == null){
            return new ResponseEntity<>(new MessageResponse("UserName/Password Not Match"),HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = JwtUtils.generateJwtToken(authentication, jwtProperties.getTokenExpiration(), jwtProperties.getSecretKey());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(
                HttpHeaders.SET_COOKIE,
                CookieUtils.createCookieString(
                        jwtProperties.getAccessTokenCookieName(),
                        jwt,
                        cookieExpireSeconds
                )
        );
        log.info(responseHeaders.toString());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        log.info(ResponseEntity.ok().headers(responseHeaders).body(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        )).getHeaders().toString());

        return ResponseEntity.ok().headers(responseHeaders).body(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken"));
        }
        if(userRepository.existsByUsername(request.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken."));
        }
        log.info("sign in:" + request.getPassword());
        Customer customer = new Customer(request.getUsername(),request.getEmail(),encoder.encode(request.getPassword()));
        Set<String> userRoles = request.getRole();
        Set<Role> roles = new HashSet<>();
        if(userRoles == null) {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            userRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        customer.setRoles(roles);
        userRepository.save(customer);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
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

    @GetMapping("/test/{id}")
    public ResponseEntity<List<UrlData>> getUserUrls(@PathVariable("id") long id) {
        Optional<Customer> optionalCustomer = userRepository.findById(id);
        if(!optionalCustomer.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<UrlData> urls = urlRepository.findByUserId((int)id);
        return ResponseEntity.ok(urls);

    }
}
