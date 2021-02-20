package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectIT {

    @Autowired
    UserService userService;

     @Autowired
    JwtUtil jwtUtil;

    User authenticatedUser;
    String token;
    HttpHeaders headers;

    Project project;

    @BeforeEach
    public void authSetup() {
        // create a user to authenticate
        authenticatedUser = new User();
        authenticatedUser.setEmail("auth.user@email.com");
        authenticatedUser.setPassword("auth_password");

        // save the user to pass authorization
        userService.createUser(authenticatedUser);

        // generate an auth token signed with the user email
        token = jwtUtil.generateToken(authenticatedUser.getEmail());

        // set up the authorization header with the auth token
        headers = new HttpHeaders();
        headers.add(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + token);
    }

    @BeforeEach
    public void setup() {
        project = Project.builder().id(1L).name("Primary proj").build();
    }

    @Nested
    @DisplayName("POST")
    class Post {

        @Test
        public void itShouldCreateProject() {

        }

    }


}
