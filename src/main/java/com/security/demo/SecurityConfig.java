package com.security.demo;

import static org.springframework.security.config.Customizer.withDefaults;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// Tells spring that this is a configuration class
// @Configuration
// Tells spring to enable web security features and allows us to configure them
// @EnableWebSecurity

// Allows inforcing security at method level. Like PreAuthorize annotation
// @EnableMethodSecurity
public class SecurityConfig {
    // Spring will inject give this a valu picked from
    // spring.datasource.url=jdbc:h2:mem:test
    @Autowired
    DataSource dataSource;

    // ********** V2 ********** JWT
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Tells spring to authorize all requests
        http.authorizeHttpRequests(
                (request) -> request.requestMatchers("/h2-console/**").permitAll().anyRequest().permitAll());

        // Avoids the use of sessions. i.2. Stateless so on every request, user has to
        // provide credentials
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // Tells spring to use http basic authentication
        http.httpBasic(withDefaults());

        // Enable h2-console
        http.csrf((csrf) -> csrf.disable());

        http.headers((headers) -> headers.frameOptions((frameOptions -> frameOptions.disable())));

        return http.build();

    }

    // ********** V1 **********
    // @Bean
    // SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // // Tells spring to authorize all requests
    // http.authorizeHttpRequests(
    // (request) ->
    // request.requestMatchers("/h2-console/**").permitAll().anyRequest().permitAll());

    // // Avoids the use of sessions. i.2. Stateless so on every request, user has
    // to
    // // provide credentials
    // http.sessionManagement((session) ->
    // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    // // Tells spring to use http basic authentication
    // http.httpBasic(withDefaults());

    // // Enable h2-console
    // http.csrf((csrf) -> csrf.disable());

    // http.headers((headers) -> headers.frameOptions((frameOptions ->
    // frameOptions.disable())));

    // return http.build();

    // }

    // OVERIDE DEFAULT BEHAVIOR
    // "******** USING INMEMORY USER DETAILS MANAGER ********"
    // @Bean
    // // User Details Service loads data from data source (Manages users in memory)
    // public UserDetailsService userDetailsService() {
    // UserDetails user1 = User.withUsername("admin")
    // .password("{noop}test")
    // .roles("ADMIN")
    // .build();

    // UserDetails user2 = User.withUsername("user")
    // .password("{noop}test")
    // .roles("USER")
    // .build();

    // return new InMemoryUserDetailsManager(user1, user2);
    // }

    // "******** USING JDBC USER DETAILS MANAGER ********"
    @Bean
    // User Details Service loads data from data source (Manages users in memory)
    public UserDetailsService userDetailsService() {
        UserDetails user1 = User.withUsername("admin")
                .password(passwordEncoder().encode("test"))
                .roles("ADMIN")
                .build();

        UserDetails user2 = User.withUsername("user")
                .password(passwordEncoder().encode("test"))
                .roles("USER")
                .build();

        System.out.println("******** USING" + dataSource.getClass().getName() + " ********");

        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.createUser(user1);
        jdbcUserDetailsManager.createUser(user2);

        return jdbcUserDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

}
