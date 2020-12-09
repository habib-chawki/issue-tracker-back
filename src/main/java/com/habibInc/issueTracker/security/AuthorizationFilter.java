package com.habibInc.issueTracker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthorizationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // intercept the request and check the Authorization header
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // extract the authorization header
        String authHeader = request.getHeader(JwtUtil.HEADER);

        // check the header and make sure it starts with 'Bearer '
        if (authHeader == null || !authHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract the token
        String token = authHeader.replace(JwtUtil.TOKEN_PREFIX, "");

        // verify the token validity and extract the subject
        String subject = jwtUtil.getSubject(token);

        // retrieve user details
        CustomUserDetails userDetails =
                (CustomUserDetails) userDetailsService.loadUserByUsername(subject);

        // extract the authenticated user from userDetails and set it as principal
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails.getAuthenticatedUser(),
                        null,
                        new ArrayList<>()
                );

        // set authentication on the security context holder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
