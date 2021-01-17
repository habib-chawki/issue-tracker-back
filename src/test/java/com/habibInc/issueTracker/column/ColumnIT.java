package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ColumnIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    JwtUtil jwtUtil;

    HttpHeaders httpHeaders;

    User authenticatedUser;
    Column column;

    @BeforeEach
    public void auth() {
        // save the authenticated user
        authenticatedUser = User.builder()
                .email("authorizedr@user.in")
                .userName("authorized")
                .password("auth_pass")
                .build();

        authenticatedUser = userService.createUser(authenticatedUser);

        // generate auth token
        String authToken = jwtUtil.generateToken(authenticatedUser.getEmail());

        // set up authorization header
        httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + authToken);
    }

    @BeforeEach
    public void setup(){
        column = new Column();
        column.setName("To do");
    }

    @Test
    public void itShouldCreateColumn() {
        Long boardId = 100L;
        String url = String.format("/boards/%s/columns", boardId);

        // given the create column post request
        HttpEntity<Column> httpEntity = new HttpEntity<>(column, httpHeaders);

        // when the request is received
        ResponseEntity<Column> response = restTemplate.exchange(url,
                HttpMethod.POST,
                httpEntity,
                Column.class
        );

        // then the column should be created successfully
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }
}
