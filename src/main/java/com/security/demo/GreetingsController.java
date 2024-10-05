package com.security.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.security.demo.jwt.JwtUtils;

@RestController
public class GreetingsController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/api/signin")
    public ResponseEntity<?> sign(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication;
        try {
            // If this passes, the user is authenticated
            // It Calls the loadUserByUsername method in the UserDetailsService
            // It Then calls passwordEncoder.matches() to compare the password
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()));

        } catch (AuthenticationException exception) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid username or password");
            response.put("status", false);
            return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
        }

        // SecurityContextHolder.getContext().setAuthentication(authentication);

        // UserDetails userDetails =
        // userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();

        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(jwt, userDetails.getUsername(), roles);

        return new ResponseEntity<Object>(loginResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/api/signup")
    public String register() {
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("test"));
        user.setEnabled(true);

        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");
        authority.setUsername(user.getUsername());

        userRepository.save(user);
        authorityRepository.save(authority);
        return "User registered";
    }

    @GetMapping("/hello")
    public String sayHello() {

        return "Hello";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userEndpoint() {
        return "Hello, user!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Hello, admin!";
    }

}
