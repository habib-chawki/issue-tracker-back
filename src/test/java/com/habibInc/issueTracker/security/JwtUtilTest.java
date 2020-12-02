package com.habibInc.issueTracker.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = JwtUtil.class)
public class JwtUtilTest {
    @Autowired
    JwtUtil jwtUtil;

    @Test
    public void itShouldGenerateJsonWebToken() {
        String jwt = jwtUtil.generateToken("Subject");

        Assertions.assertThat(jwt).isNotNull();
    }
}
