package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.ApiError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    User user;

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
    public void itShouldCreateUser() {
        ResponseEntity<User> response =
                restTemplate.postForEntity("/users", user, User.class);

        // expect user to have been properly and successfully created
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(user);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
    }

    @Test
    public void itShouldGetUserById() {
        // given a user is created
        User savedUser = userService.createUser(user);

        // when a get request is made to retrieve the user by id
        ResponseEntity<User> response =
                restTemplate.getForEntity("/users/" + savedUser.getId(), User.class);

        // then the proper user should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(user);
        assertThat(response.getBody().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    public void itShouldReturnUserNotFoundError() {
        // given an error message
        String errorMessage = "User not found";

        // when a post request with a user id that does not exist is made
        ResponseEntity<ApiError> response =
                restTemplate.getForEntity("/users/10", ApiError.class);

        // then a 404 user not found error should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).isEqualToIgnoringCase(errorMessage);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    public void itShouldReturnInvalidUserIdError() {
        // given an error message
        String errorMessage = "Invalid user id";

        // when a get request with an invalid user id is made
        ResponseEntity<ApiError> response =
                restTemplate.getForEntity("/users/invalid", ApiError.class);

        // then a 400 invalid user id error should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorMessage()).isEqualToIgnoringCase(errorMessage);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    public void itShouldHashUserPassword() {
        // given a saved user
        User savedUser = userService.createUser(user);

        // when a get request is made with the saved user id
        ResponseEntity<User> response =
                restTemplate.getForEntity("/users/" + savedUser.getId(), User.class);

        // then the password should be hashed and matches the plain text
        boolean match =
                bCryptPasswordEncoder.matches("MyPassword", savedUser.getPassword());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(match).isTrue();
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }
}
