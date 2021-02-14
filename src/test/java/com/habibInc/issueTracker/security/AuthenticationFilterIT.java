package com.habibInc.issueTracker.security;

import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthenticationFilterIT {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestRestTemplate restTemplate;

    User user;
    AuthenticationRequest authenticationRequest;

    @BeforeEach
    public void setup() {
        user = new User();

        user.setFullName("first-last");
        user.setUserName("my_username");
        user.setEmail("my_email@email.com");
        user.setPassword("MyPassword");
    }

    @Test
    public void itShouldLogUserIn() {
        // given the user is created
        userService.createUser(user);

        // given the login request
        authenticationRequest =
                new AuthenticationRequest(user.getEmail(), "MyPassword");

        // when the login request is made
        ResponseEntity<String> res = restTemplate.postForEntity(
                "/login",
                authenticationRequest,
                String.class
        );

        // then login should be successful and the response should contain an auth token
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getHeaders().containsKey(JwtUtil.HEADER)).isTrue();
        assertThat(res.getHeaders().get(JwtUtil.HEADER)).isNotNull();
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }
}
