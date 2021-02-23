package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectRepository;
import com.habibInc.issueTracker.project.ProjectService;
import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SprintIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    JwtUtil jwtUtil;

    Sprint sprint;
    Project project;
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

    @BeforeAll
    public void globalSetup() {
        // set up a project
        project = new Project();
        project.setName("Project");

        // save the project
        project = projectService.createProject(project, authenticatedUser);
    }

    @BeforeEach
    public void setup() {
        sprint = Sprint.builder()
                .name("Primary sprint")
                .goal("Primary goal")
                .project(project)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Nested
    @DisplayName("POST")
    class post {

        private String baseUrl = "/projects/" + project.getId() + "/sprints";
        private HttpEntity httpEntity;

        @BeforeEach
        public void setup() {
            httpEntity = new HttpEntity(sprint, headers);
        }

        @Test
        public void itShouldCreateSprint() {
            ResponseEntity<Sprint> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, Sprint.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
    }

    @AfterAll
    public void globalTeardown() {
        projectRepository.deleteAll();
    }

    @AfterAll
    public void authTeardown() {
        userRepository.deleteAll();
    }
}
