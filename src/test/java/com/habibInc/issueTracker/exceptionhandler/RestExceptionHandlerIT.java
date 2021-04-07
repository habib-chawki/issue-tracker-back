package com.habibInc.issueTracker.exceptionhandler;

import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestExceptionHandlerIT {

    @Autowired
    UserService userService;

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
}
