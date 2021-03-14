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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

            // given the list of issues ids
            List<Long> issuesIds = issues.stream().map((issue) -> issue.getId()).collect(Collectors.toList());

            // given the sprint backlog is set via a PATCH request
            HttpEntity<Long[]> httpPatchEntity = new HttpEntity(issuesIds, headers);
            restTemplate.exchange(
                    baseUrl + "/" + sprint.getId() + "/backlog",
                    HttpMethod.PATCH,
                    httpPatchEntity,
                    Void.class
            );

            // when a GET request is made to fetch the sprint by id
            ResponseEntity<Sprint> response =
                    restTemplate.exchange(baseUrl + "/" + sprint.getId(), HttpMethod.GET, httpEntity, Sprint.class);

            // then expect the list of sprint issues to have been retrieved along with it
            assertThat(response.getBody().getBacklog()).isEqualTo(issues);
        }

        @Test
        public void itShouldGetSprintsByStatus() {
            // given a list of active sprints
            List<Sprint> activeSprints = List.of(
                    Sprint.builder().name("sprint active 1").status(SprintStatus.ACTIVE).build(),
                    Sprint.builder().name("sprint active 2").status(SprintStatus.ACTIVE).build()
            );

            // given a list of inactive sprints
            List<Sprint> inactiveSprints = List.of(
                    Sprint.builder().name("sprint inactive 1").status(SprintStatus.INACTIVE).build(),
                    Sprint.builder().name("sprint inactive 2").status(SprintStatus.INACTIVE).build()
            );

            // given a list of over sprints
            List<Sprint> overSprints = List.of(
                    Sprint.builder().name("sprint over 1").status(SprintStatus.OVER).build(),
                    Sprint.builder().name("sprint over 2").status(SprintStatus.OVER).build()
            );

            activeSprints = (List<Sprint>) sprintRepository.saveAll(activeSprints);
            inactiveSprints = (List<Sprint>) sprintRepository.saveAll(inactiveSprints);
            overSprints = (List<Sprint>) sprintRepository.saveAll(overSprints);

            // when a GET request is made to fetch sprints by status
            ResponseEntity<Sprint[]> response =
                    restTemplate.exchange(baseUrl + "?status=active", HttpMethod.GET, httpEntity, Sprint[].class);

            List<Sprint> sprintsByStatus = Arrays.asList(response.getBody());

            // then expect only the sprints with the correct status to have been fetched
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            assertThat(sprintsByStatus)
                    .containsExactlyElementsOf(activeSprints)
                    .doesNotContainAnyElementsOf(inactiveSprints)
                    .doesNotContainAnyElementsOf(overSprints);
        }
    }

    @Nested
    @DisplayName("PATCH")
    class Patch {

        private String baseUrl = "/projects/" + project.getId() + "/sprints";

        @Test
        public void itShouldSetSprintBacklog() {
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

            // given the url
            String url = baseUrl + "/" + sprint.getId() + "/backlog";

            // given the request body containing the list of issues ids
            List<Long> issuesIds = issues.stream().map((issue) -> issue.getId()).collect(Collectors.toList());
            HttpEntity<List<Long>> httpEntity = new HttpEntity<>(issuesIds, headers);

            // when a PATCH request is made to set the sprint backlog
            ResponseEntity<String> patchResponse =
                    restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, String.class);

            // then expect the sprint issues to have been set successfully
            assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

            List<Issue> sprintIssues =
                    sprintService.getSprintById(sprint.getId()).getBacklog();
            assertThat(sprintIssues).containsExactlyElementsOf(issues);
        }

        @Test
        public void itShouldUpdateSprintStatus() {
            // given the sprint is created
            sprint = sprintService.createSprint(project.getId(), sprint);

            // given the request body with the new sprint status
            String requestBody = "{\"newSprintStatus\": \"active\"}";
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);

            // when a PATCH request is made to update the sprint status
            ResponseEntity<Sprint> response =
                    restTemplate.exchange(baseUrl + "/" + sprint.getId(), HttpMethod.PATCH, httpEntity, Sprint.class);

            // then the sprint status should be updated successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getStatus()).isEqualTo(SprintStatus.ACTIVE);
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
