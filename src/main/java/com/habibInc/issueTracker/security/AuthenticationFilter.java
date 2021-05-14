package com.habibInc.issueTracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            // extract the authentication request from the json request body
            AuthenticationRequest authenticationRequest =
                    new ObjectMapper().readValue(request.getInputStream(), AuthenticationRequest.class);

            // create an authentication object with the extracted email and password from the request body
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(),
                    authenticationRequest.getPassword(),
                    new ArrayList<>()
            );

            // return the authentication
            return authenticationManager.authenticate(authentication);

        } catch (IOException e) {
            throw new RuntimeException("Authentication failed");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        // retrieve the authenticated user
        CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();
        User authenticatedUser = principal.getAuthenticatedUser();

        // set up the user DTO response body
        UserDto userDto = new UserDto(
                authenticatedUser.getId(),
                authenticatedUser.getUsername(),
                authenticatedUser.getFullName()
        );

        String responseBody = new ObjectMapper().writeValueAsString(userDto);

        // generate the auth token
        String subject = authResult.getName();
        String token = jwtUtil.generateToken(subject);

        // embed the token in an authorization header
        response.addHeader(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + token);

        // set the response body
        response.setContentType("application/json");
        response.getWriter().print(responseBody);
    }
}
