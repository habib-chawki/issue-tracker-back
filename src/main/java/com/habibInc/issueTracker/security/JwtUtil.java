package com.habibInc.issueTracker.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

//@ConfigurationProperties(prefix = "properties.jwt")
@Service
public class JwtUtil {
    public static String secretKey = "thisismysecretfuckingkeyhere";

    public JwtUtil() {
    }

    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.valueOf(LocalDate.now()))
                .setExpiration(Date.valueOf(LocalDate.now().plusWeeks(2)))
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
}
