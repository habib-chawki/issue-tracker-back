package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectRepository;
import com.habibInc.issueTracker.project.ProjectService;
import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    SprintService sprintService;

    @Autowired
    SprintRepository sprintRepository;

    @Autowired
    IssueRepository issueRepository;

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
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .build();
    }

    @Nested
    @DisplayName("POST")
    class post {

        private String baseUrl = "/projects/" + project.getId() + "/sprints";
        private HttpEntity httpEntity;

        @Test
        public void itShouldCreateSprint() {
            // given the request body
            httpEntity = new HttpEntity(sprint, headers);

            // when a POST request is made to create a new sprint
            ResponseEntity<Sprint> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, Sprint.class);

            // then expect the sprint to have been created successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getId()).isNotNull().isPositive();
            assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(sprint);
        }

        @Test
        public void itShouldSetSprintIssues() {
            // given a list of issues
            List<Issue> issues = List.of(
                    Issue.builder().summary("issue 1").build(),
                    Issue.builder().summary("issue 2").build(),
                    Issue.builder().summary("issue 3").build()
            );

            // given the issues are saved
            issues = (List<Issue>) issueRepository.saveAll(issues);

            // given the sprint is saved
            sprint = sprintService.createSprint(project.getId(), sprint);

            // given the url
            String url = baseUrl + "/" + sprint.getId() + "/issues";

            // given the request body containing the list of issues
            httpEntity = new HttpEntity(issues, headers);

            // when a POST request is made to set the sprint issues
            ResponseEntity<Void> postResponse =
                    restTemplate.postForEntity(url, httpEntity, Void.class);

            // then expect the sprint issues to have been set successfully
            assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            List<Issue> sprintIssues =
                    sprintService.getSprintById(sprint.getId()).getIssues();
            assertThat(sprintIssues).containsExactlyElementsOf(issues);
        }
    }

    @Nested
    @DisplayName("GET")
    class Get {

        private String baseUrl = "/projects/" + project.getId() + "/sprints";
        private HttpEntity httpEntity;

        @BeforeEach
        public void setup() {
            httpEntity = new HttpEntity(headers);
        }

        @Test
        public void itShouldGetSprint() {
            // given the sprint is saved
            sprint = sprintService.createSprint(project.getId(), sprint);

            // when a GET request is made to fetch a sprint by id
            ResponseEntity<Sprint> response =
                    restTemplate.exchange(baseUrl + "/" + sprint.getId(), HttpMethod.GET, httpEntity, Sprint.class);

            // then expect the sprint to have been retrieved successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sprint);
        }

        @Test
        public void itShouldGetSprintByIdAlongWithItsIssues() {
            // given a list of issues
            List<Issue> issues = List.of(
                    Issue.builder().summary("issue 1").build(),
                    Issue.builder().summary("issue 2").build(),
                    Issue.builder().summary("issue 3").build()
            );

            // given the issues are saved
            issues = (List<Issue>) issueRepository.saveAll(issues);

            // given the sprint is created
            sprint = sprintService.createSprint(project.getId(), sprint);

            // given the sprint issues are set via a POST request
            HttpEntity<Issue[]> httpPostEntity = new HttpEntity(issues, headers);
            restTemplate.postForEntity(baseUrl + "/" + sprint.getId() + "/issues", httpPostEntity, Void.class);

            // when a GET request is made to fetch the sprint by id
            ResponseEntity<Sprint> response =
                    restTemplate.exchange(baseUrl + "/" + sprint.getId(), HttpMethod.GET, httpEntity, Sprint.class);

            // then expect the list of sprint issues to have been retrieved along with it
            assertThat(response.getBody().getIssues()).isEqualTo(issues);
        }
    }

    @AfterEach
    public void teardown() {
        issueRepository.deleteAll();
        sprintRepository.deleteAll();
    }

    @AfterAll
    public void authTeardown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }
}
