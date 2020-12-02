package com.habibInc.issueTracker.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    @Value("${secret.key}")
    String secretKey;

    // intercept the request and check the Authorization header
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // extract the authorization header
        String authHeader = request.getHeader("Authorization");

        // check the header and make sure it starts with 'Bearer '
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // extract the token
        String token = authHeader.replace("Bearer ", "");

        // verify the token validity
        String subject = Jwts.parser()
                                .setSigningKey(secretKey)
                                .parseClaimsJws(token)
                                .getBody()
                                .getSubject();

        // extract the principal from the auth token
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(subject, null);

        // set authentication on the security context holder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
