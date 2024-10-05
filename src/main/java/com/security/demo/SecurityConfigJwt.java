package com.security.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.security.demo.jwt.AuthEntryPoint;
import com.security.demo.jwt.AuthtokenFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfigJwt {
    @Autowired
    private DataSource dataSource;

    @Autowired
    // In Spring Security, this interface defines the behavior to be executed when
    // an authentication attempt fails.
    // It provides a single method: commence(HttpServletRequest request,
    // HttpServletResponse response, AuthenticationException authException).
    // This method is invoked when an authentication attempt is unsuccessful,
    // allowing you to customize the response sent back to the client.
    // ****** TRIGGERED WHEN A USER TRIES TO ACCESS A PROTECTED RESOURCE WITHOUT
    // AUTHENTICATION BY ******
    private AuthEntryPoint unauthorizedHandler;

    @Bean
    // Checking if the token is valid for protected routes
    public AuthtokenFilter authenticationJwtTokenFilter() {
        return new AuthtokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/h2-console/**")
                .permitAll()
                .requestMatchers("/api/signup")
                .permitAll()
                .requestMatchers("/api/signin")
                .permitAll()
                .anyRequest()
                .authenticated());

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Set the handler for unauthorized requests
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.headers(header -> header.frameOptions(frame -> frame.sameOrigin()));
        http.csrf(csrf -> csrf.disable());

        // Add the custom filter to the security filter chain
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }

    @Bean
    // User Details Service loads data from data source (Manages users in memory)
    public UserDetailsService userDetailsService() {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
