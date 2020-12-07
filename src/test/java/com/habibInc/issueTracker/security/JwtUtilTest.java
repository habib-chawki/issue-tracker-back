package com.habibInc.issueTracker.security;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = JwtUtil.class)
public class JwtUtilTest {
    @Autowired
    JwtUtil jwtUtil;

    @Test
    public void itShouldGenerateJwt() {
        String jwt = jwtUtil.generateToken("Subject");

        assertThat(jwt).isNotNull();

        String subject = jwtUtil.getSubject(jwt);

        assertThat(subject).isEqualTo("Subject");
    }

    @Test
    public void itShouldVerifyJwt() {
        String token = Jwts.builder()
                .setSubject("Habib")
                .signWith(SignatureAlgorithm.HS256, JwtUtil.secretKey)
                .compact();

        Jws<Claims> claimsJws = jwtUtil.verifyToken(token);

        assertThat(claimsJws.getHeader().getAlgorithm()).isEqualTo("HS256");
        assertThat(claimsJws.getBody().getSubject()).isEqualTo("Habib");
        assertThat(claimsJws.getSignature()).isNotNull();
    }

    @Test
    public void itShouldGetJwtSubject() {
        String token = Jwts.builder()
                .setSubject("Chawki")
                .signWith(SignatureAlgorithm.HS256, JwtUtil.secretKey)
                .compact();

        String subject = jwtUtil.getSubject(token);

        assertThat(subject).isEqualTo("Chawki");
    }

    @Test
    public void itShouldReturnMalformedJwtError() {
        assertThrows(MalformedJwtException.class,
                () -> jwtUtil.verifyToken("invalid_token"));
    }
}
