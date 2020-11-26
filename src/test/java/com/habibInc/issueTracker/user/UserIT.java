package com.habibInc.issueTracker.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void setup() {
        user = new User();

        user.setFirstName("first");
        user.setLastName("last");
        user.setUserName("my_username");
        user.setEmail("my_email@email.com");
        user.setPassword("this is it");
    }

    @Test
    public void itShouldCreateUser() {
        ResponseEntity<User> response =
                restTemplate.postForEntity("/users", user, User.class);

        // expect user to have been properly and successfully created
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(user);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }
}
