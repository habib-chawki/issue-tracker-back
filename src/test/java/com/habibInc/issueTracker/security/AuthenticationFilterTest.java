package com.habibInc.issueTracker.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.springframework.boot.test.context.SpringBootTest.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthenticationFilterTest {
    @Autowired
    UserService userService;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper mapper;

    User user, createdUser;
    AuthenticationRequest authenticationRequest;

    @BeforeEach
    public void setup() {
        user = new User();

        user.setFirstName("first");
        user.setLastName("last");
        user.setUserName("my_username");
        user.setEmail("my_email@email.com");
        user.setPassword("MyPassword");

        createdUser = userService.createUser(user);

        authenticationRequest = new AuthenticationRequest();

        authenticationRequest.setEmail(user.getEmail());
        authenticationRequest.setPassword(user.getPassword());
    }

    @Test
    public void itShouldLoginUser() throws Exception {
        String requestBody = mapper.writeValueAsString(authenticationRequest);

        ResponseEntity<String> res =
                restTemplate.postForEntity("/login", authenticationRequest, String.class);

        System.out.println("RESPONSE" + res);
    }
}
