package com.security.demo.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// A Custom Filter
@Component
public class AuthtokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthEntryPoint unauthorizedHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    // THIS IS A CUSTOM FILTER
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request,
            @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);

            // There is a user with a token and create an authentication object and set it
            // in the security context
            if (jwt != null) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Creates an authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, userDetails.getAuthorities());

                // Set the details of the request Stores additional details about the
                // authentication request. These might be an IP address, certificate serial
                // number etc.

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication object in the security context(Like req.user in
                // express)
                // Authentication user for the duration of request
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            AuthenticationException authException = new AuthenticationServiceException(e.getMessage());
            // If there is an exception, the unauthorizedHandler will be called to handle it
            // and send a response
            unauthorizedHandler.commence(request, response, authException);
        }

        // Continue with the request
        // There are other filters, so we need to call the next filter in the chain
        filterChain.doFilter(request, response);

    }

    private String parseJwt(HttpServletRequest request) {
        return jwtUtils.getJwtFromHeader(request);
    }

}
