package com.habibInc.issueTracker.security;

import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserDto;
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

        // create the user
        user = userService.createUser(user);

        // set up the login request
        authenticationRequest =
                new AuthenticationRequest(user.getEmail(), "MyPassword");
    }

    @Test
    public void itShouldLogUserIn() {
        // when the login request is made with valid credentials
        ResponseEntity<UserDto> res = restTemplate.postForEntity(
                "/login",
                authenticationRequest,
                UserDto.class
        );

        // then login should be successful
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void whenUserIsLoggedInSuccessfully_itShouldRespondWithTheAuthToken () {
        // when the login request is made with valid credentials
        ResponseEntity<UserDto> res = restTemplate.postForEntity(
                "/login",
                authenticationRequest,
                UserDto.class
        );

        // then the response should contain the auth token header
        assertThat(res.getHeaders().containsKey(JwtUtil.HEADER)).isTrue();
        assertThat(res.getHeaders().get(JwtUtil.HEADER)).isNotNull();
    }

    @Test
    public void whenUserIsLoggedInSuccessfully_itShouldRespondWithUserDto() {
        ResponseEntity<UserDto> res = restTemplate.postForEntity(
                "/login",
                authenticationRequest,
                UserDto.class
        );

        assertThat(res.getBody().getId()).isEqualTo(user.getId());
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }
}
