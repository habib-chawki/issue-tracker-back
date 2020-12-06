package com.habibInc.issueTracker.security;

import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
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
public class AuthenticationFilterTest {
    @Autowired
    UserService userService;

    @Autowired
    TestRestTemplate restTemplate;

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
    }

    @Test
    public void itShouldLoginUser() {
        createdUser = userService.createUser(user);

        authenticationRequest = new AuthenticationRequest(
                "my_email@email.com", "MyPassword"
        );

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/login",
                authenticationRequest,
                String.class
        );

        System.out.println("RESPONSE ==> " + res);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
