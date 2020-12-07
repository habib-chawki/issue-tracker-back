package com.habibInc.issueTracker.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class JwtUtil {
    public static final String HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final Date EXPIRATION_TIME = Date.valueOf(LocalDate.now().plusWeeks(2));

    @Value("${secretKey}")
    private String secretKey;

    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.valueOf(LocalDate.now()))
                .setExpiration(EXPIRATION_TIME)
                .signWith(SignatureAlgorithm.HS256, this.secretKey)
                .compact();
    }

    public Jws<Claims> verifyToken(String token) {
        try {
             return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
        } catch(Exception ex){
            throw new MalformedJwtException("Unauthorized");
        }
    }

    public String getSubject(String token) throws MalformedJwtException{
        return verifyToken(token).getBody().getSubject();
    }

    public String getSecretKey() {
        return secretKey;
    }
}
