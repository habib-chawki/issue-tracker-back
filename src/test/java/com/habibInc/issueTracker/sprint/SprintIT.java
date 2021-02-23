package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SprintIT {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    User authenticatedUser;
    HttpHeaders headers;

    @BeforeAll
    public void authSetup() {
        // set up an authenticated user
        authenticatedUser = new User();
        authenticatedUser.setEmail("auth@user.me");
        authenticatedUser.setPassword("auth.pass");

        userService.createUser(authenticatedUser);

        // generate auth token
        String token = jwtUtil.generateToken(authenticatedUser.getEmail());

        // add Authorization request header
        headers = new HttpHeaders();
        headers.add(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + token);
    }

    @AfterAll
    public void authTeardown() {
        userRepository.deleteAll();
    }

}
