package com.habibInc.issueTracker.exceptionhandler;

import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestExceptionHandlerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    User authenticatedUser;
    HttpHeaders headers;
    String token;

    @BeforeEach
    public void authSetup() {
        // set up the authenticated user
        authenticatedUser = User.builder()
                .fullName("authenticated user")
                .userName("auth_user")
                .email("auth@user.email")
                .password("@uth_p@$$")
                .build();

        authenticatedUser = userService.createUser(authenticatedUser);

        // set up authorization header
        headers = new HttpHeaders();
        token = jwtUtil.generateToken(authenticatedUser.getEmail());
        headers.add(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + token);
    }

    @Test
    public void givenUserSignup_whenUserAlreadyExists_itShouldReturnDuplicateKeyError() {
        // given a signup request of a user that already exists
        User user = User.builder()
                .email(authenticatedUser.getEmail())
                .password(authenticatedUser.getPassword())
                .fullName(authenticatedUser.getFullName())
                .userName(authenticatedUser.getUserName())
                .build();

        // given the request body
        HttpEntity httpEntity = new HttpEntity(user, headers);

        // given the signup url
        String signupUrl = "/users/signup";

        // when the signup request is made
        ResponseEntity<ApiError> response =
                restTemplate.exchange(signupUrl, HttpMethod.POST, httpEntity, ApiError.class);

        // then expect a 409 Conflict error
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorMessage()).isEqualTo("Duplicate key");
    }

    @Test
    public void givenGetUserById_whenUserDoesNotExist_itShouldReturnUserNotFoundError() {
        // set up authorization header
        HttpEntity httpEntity = new HttpEntity<>(headers);

        // given an error message
        String errorMessage = "User not found";

        // when a POST request with a user id that does not exist is made
        ResponseEntity<ApiError> response = restTemplate.exchange(
                "/users/404",
                HttpMethod.GET,
                httpEntity,
                ApiError.class
        );

        // then a 404 user not found error should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).isEqualToIgnoringCase(errorMessage);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    public void givenGetIssueById_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
        HttpEntity httpEntity = new HttpEntity<>(headers);

        // when a request for an issue that does not exist is received
        ResponseEntity<ApiError> response = restTemplate.exchange(
                "/issues/" + 404L,
                HttpMethod.GET,
                httpEntity,
                ApiError.class
        );

        // then the response should be a 404 issue not found error
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Issue not found");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }
}
