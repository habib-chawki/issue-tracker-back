package com.habibInc.issueTracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private ObjectMapper mapper;

    @Value("secretKey")
    private String secretKey;

    @Autowired
    public AuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            // extract the authentication request from the json request body
            AuthenticationRequest authenticationRequest =
                    mapper.readValue(request.getInputStream(), AuthenticationRequest.class);

            // create an authentication object with the extracted email and password from the request body
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(),
                    authenticationRequest.getPassword()
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
                                            Authentication authResult) throws IOException, ServletException {
        // generate the auth token
        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .setIssuedAt(Date.valueOf(LocalDate.now()))
                .setExpiration(Date.valueOf(LocalDate.now().plusWeeks(2)))
                .signWith(SignatureAlgorithm.HS256, this.secretKey)
                .compact();

        // embed the auth token in an authorization header
        response.addHeader("Authorization", "Bearer " + token);
    }
}
