package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtil jwtUtil;

    User authenticatedUser;
    String token;
    HttpHeaders headers;

    Project project, project2;

    @BeforeAll
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
        project = new Project();
        project.setName("Primary project");

        project2 = new Project();
        project2.setName("Secondary project");
    }

    @Nested
    @DisplayName("POST")
    class Post {

        private final String baseUrl = "/projects";
        HttpEntity<Project> httpEntity;

        @BeforeEach
        public void setup() {
            httpEntity = new HttpEntity<>(project, headers);
        }

        @Test
        public void itShouldCreateProject() {
            // when a POST request to create a new project is made
            ResponseEntity<Project> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, Project.class);

            // then expect the response to be the created project with an autogenerated id and the authenticated user set as owner
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getId()).isNotNull().isPositive();
        }

        @Test
        public void givenCreateProject_itShouldSetAuthenticatedUserAsProjectOwner() {
            // given the POST request to create the project
            ResponseEntity<Project> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, Project.class);

            // when the project is created
            Project createdProject =
                    projectService.getProjectById(response.getBody().getId());

            // then expect the authenticated user to have been set as project owner
            assertThat(createdProject.getOwner()).isEqualTo(authenticatedUser);
        }
    }

    @Nested
    @DisplayName("GET")
    class Get {

        private final String baseUrl = "/projects";
        HttpEntity<Project> httpEntity;

        @BeforeEach
        public void setup() {
            httpEntity = new HttpEntity<>(headers);
        }

        @Test
        public void itShouldGetProjectById() {
            // given the project is created
            project = projectService.createProject(project, authenticatedUser);

            // when a GET request to fetch the project by id is made
            ResponseEntity<Project> response = restTemplate.exchange(
                    baseUrl + "/" + project.getId(),
                    HttpMethod.GET,
                    httpEntity,
                    Project.class
            );

            // then the project should be retrieved successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(project);
        }

        @Test
        public void givenGetProjectById_whenProjectDoesNotExist_itShouldReturnProjectNotFoundError() {
            // when the project does not exist
            ResponseEntity<ApiError> response = restTemplate.exchange(
                    baseUrl + "/404",
                    HttpMethod.GET,
                    httpEntity,
                    ApiError.class
            );

            // then expect a 404 project not found error
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().getErrorMessage()).contains("Project not found");
        }

        @Test
        public void itShouldGetListOfAllProjects() {
            // given a list of projects
            List<Project> projects = List.of(project, project2);
            projects = (List<Project>) projectRepository.saveAll(projects);

            // when a GET request to fetch all projects is made
            ResponseEntity<Project[]> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    httpEntity,
                    Project[].class
            );

            // then expect the response to be the list of projects
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSameElementsAs(projects);
        }

        @Test
        public void itShouldGetProjectBacklog() {
            // given the project is saved
            project = projectService.createProject(project, authenticatedUser);

            // given the GET backlog url
            String url = String.format("%s/%s/backlog", baseUrl, project.getId());

            // given the project backlog
            List<Issue> backlog = List.of(
                    Issue.builder().project(project).summary("issue 1").build(),
                    Issue.builder().project(project).summary("issue 2").build(),
                    Issue.builder().project(project).summary("issue 2").build()
            );

            // given the backlog is saved
            backlog = (List<Issue>) issueRepository.saveAll(backlog);

            // when a GET request to fetch the project backlog is made
            ResponseEntity<Issue[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    httpEntity,
                    Issue[].class
            );

            // then expect the backlog to have been retrieved successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSameElementsAs(backlog);
        }
    }

    @AfterEach
    public void teardown() {
        issueRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @AfterAll
    public void authTeardown() {
        userRepository.deleteAll();
    }
}
