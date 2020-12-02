package com.habibInc.issueTracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = JwtUtil.class)
public class JwtUtilTest {
    @Autowired
    JwtUtil jwtUtil;

    @Value("${secret.key}")
    String secretKey;

    @Test
    public void itShouldGenerateJwt() {
        String jwt = jwtUtil.generateToken("Subject");

        assertThat(jwt).isNotNull();
    }

    @Test
    public void itShouldVerifyJwt() {
        String token = Jwts.builder()
                .setSubject("Habib")
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Jws<Claims> claimsJws = jwtUtil.verifyToken(token);

        assertThat(claimsJws.getHeader().getAlgorithm()).isEqualTo("HS256");
        assertThat(claimsJws.getBody().getSubject()).isEqualTo("Habib");
        assertThat(claimsJws.getSignature()).isNotNull();
    }
}
