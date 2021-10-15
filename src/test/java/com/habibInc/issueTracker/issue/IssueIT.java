package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectRepository;
import com.habibInc.issueTracker.project.ProjectService;
import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IssueIT {
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    IssueService issueService;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ModelMapper modelMapper;

    User authenticatedUser;
    Issue issue1, issue2;
    Project project;

    String token;
    HttpHeaders headers;

    @BeforeAll
    public void authSetup() {
        // create a user to authenticate
        authenticatedUser = new User();
        authenticatedUser.setEmail("authenticated@user.me");
        authenticatedUser.setPassword("auth_password");
        authenticatedUser.setFullName("auth full name");
        authenticatedUser.setUsername("auth username");

        // save the user to pass authorization
        authenticatedUser = userService.createUser(authenticatedUser);

        // generate an auth token signed with the user email
        token = jwtUtil.generateToken(authenticatedUser.getEmail());

        // set up the authorization header with the auth token
        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
    }

    @BeforeEach
    public void setup() {
        // create issues
        issue1 = new Issue();
        issue2 = new Issue();

        // set up issue1 properties
        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");
        issue1.setType(IssueType.STORY);
        issue1.setStatus(IssueStatus.RESOLVED);
        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setPoints(4);

        // set up issue2 properties
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setStatus(IssueStatus.IN_PROGRESS);
        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setPoints(6);

        // set up a project
        project = new Project();
        project.setName("Primary project");

        // save the project
        project = projectService.createProject(project, authenticatedUser);
    }

    @Nested
    @DisplayName("POST")
    class Post {

        private HttpEntity<Issue> httpEntity;
        private String baseUrl;

        @BeforeEach
        public void setup() {
            // set up the request
            httpEntity = new HttpEntity<>(issue1, headers);
            baseUrl = String.format("/issues?project=%d", project.getId());
        }

        @Test
        @DisplayName("Create issue")
        public void itShouldCreateIssue() {
            // given the expected response
            IssueDto issueDtoResponse = modelMapper.map(issue1, IssueDto.class);

            // when a POST request to create an issue is made
            ResponseEntity<IssueDto> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, IssueDto.class);

            // then expect the issue to have been created successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getId()).isNotNull().isPositive();
            assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(issueDtoResponse);
        }

        @Test
        @DisplayName("Set the authenticated user as issue reporter")
        public void givenCreateIssue_itShouldSetTheAuthenticatedUserAsReporter() {
            // when an issue is created after a POST request
            ResponseEntity<Issue> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, Issue.class);

            // then the created issue's reporter should be the authenticated user
            assertThat(issueService.getIssueById(response.getBody().getId()).getReporter())
                    .isEqualTo(authenticatedUser);
        }

        @Test
        public void givenCreateIssue_itShouldSetTheIssueProject() {
            // when an issue is created after a POST request
            ResponseEntity<Issue> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, Issue.class);

            // then the issue project should be set
            assertThat(issueService.getIssueById(response.getBody().getId()).getProject())
                    .isEqualTo(project);
        }

        @Test
        public void givenCreateIssue_itShouldSetTheAssignee() {
            // given the assignee
            User assignee = userService.createUser(
                    User.builder()
                    .email("assignee@issue")
                    .password("@$$ignee")
                    .fullName("issue assignee")
                    .username("issue@assignee")
                    .build()
            );

            // given the issue assignee is set
            issue1.setAssignee(assignee);

            // given the request body
            httpEntity = new HttpEntity<>(issue1, headers);

            // when an issue is created after a POST request
            ResponseEntity<IssueDto> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, IssueDto.class);

            // then expect the assignee to have been set
            assertThat(response.getBody().getAssignee()).isEqualToComparingOnlyGivenFields(assignee);
        }

        @Test
        public void givenCreateIssue_itShouldSetItsPosition() {
            // when a POST request is made to create an issue
            final ResponseEntity<IssueDto> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, IssueDto.class);

            // then expect the issue's position to have been set
            assertThat(response.getBody().getPosition()).isEqualTo(1);
        }

        @Test
        public void givenCreateIssue_itShouldPutIssueInLastPosition() {
            // given a list of project issues
            List<Issue> issues = (List<Issue>) issueRepository.saveAll(
                    List.of(
                            Issue.builder().project(project).summary("issue 1").build(),
                            Issue.builder().project(project).summary("issue 2").build(),
                            Issue.builder().project(project).summary("issue 3").build()
                    )
            );

            // when a POST request is made to create a new issue
            final ResponseEntity<IssueDto> response =
                    restTemplate.postForEntity(baseUrl, httpEntity, IssueDto.class);

            // then the issue's position should be last
            assertThat(response.getBody().getPosition()).isEqualTo(issues.size() + 1);
        }
    }

    @Nested
    @DisplayName("GET")
    class Get {

        HttpEntity<Void> httpEntity;

        @BeforeEach
        public void setup(){
            // set up authorization header
            httpEntity = new HttpEntity<>(headers);
        }

        @Test
        public void itShouldGetIssueById() {
            // given the issue is created
            issue2 = issueService.createIssue(issue2, authenticatedUser, project.getId());

            // when a GET request is made to retrieve an issue by id
            ResponseEntity<Issue> response = restTemplate.exchange(
                    "/issues/" + issue2.getId(),
                    HttpMethod.GET,
                    httpEntity,
                    Issue.class
            );

            // then expect the proper issue to have been retrieved
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getId()).isPositive();
            assertThat(response.getBody()).isEqualTo(issue2);

            // expect the response to contain a list of comments
            assertThat(response.getBody()).hasFieldOrProperty("comments");
        }

        @Test
        public void itShouldGetAllIssues() {
            // given a list of issues
            List<Issue> issues = Arrays.asList(issue1, issue2);
            issueRepository.saveAll(issues);

            // when a GET request to fetch the list of all issues is made
            ResponseEntity<Issue[]> response = restTemplate.exchange(
                    "/issues",
                    HttpMethod.GET,
                    httpEntity,
                    Issue[].class
            );

            List<Issue> responseBody = Arrays.asList(response.getBody());

            // then expect all issues to have been retrieved
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(responseBody).isEqualTo(issues);
        }
    }

    @Nested
    @DisplayName("PUT")
    class Update {

        @Test
        public void itShouldUpdateIssue() throws JsonProcessingException {
            // given the issue is created
            Issue issue = issueService.createIssue(issue1, authenticatedUser, project.getId());

            // given the updated issue fields
            issue1.setSummary("updated summary");
            issue1.setType(IssueType.BUG);

            // given the request body
            HttpEntity<Issue> httpEntity = new HttpEntity<>(issue1, headers);

            // given the expected response
            IssueDto updatedIssue = modelMapper.map(issue1, IssueDto.class);

            // when a PUT request is made to update an issue
            ResponseEntity<IssueDto> response = restTemplate.exchange(
                    "/issues/" + issue.getId(),
                    HttpMethod.PUT,
                    httpEntity,
                    IssueDto.class
            );

            // the response should be the updated issue DTO
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(updatedIssue);
        }

        @Test
        public void givenUpdateIssue_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
            // set up the request body and headers
            HttpEntity<Issue> httpEntity = new HttpEntity<>(issue1, headers);

            // when a put request is made with an id of an issue that does not exist
            ResponseEntity<ApiError> response = restTemplate.exchange(
                    "/issues/" + 404L,
                    HttpMethod.PUT,
                    httpEntity,
                    ApiError.class
            );

            // then the response should be a 404 issue not found error
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Issue not found");
        }

        @Test
        public void whenAuthenticatedUserIsNotTheReporter_itShouldNotAllowIssueUpdate() throws JsonProcessingException {
            // given a random reporter who's not the authenticated user
            User randomReporter = new User();

            randomReporter.setEmail("random.user@email.com");
            randomReporter.setPassword("random_pass");
            randomReporter.setFullName("random user");
            randomReporter.setUsername("random_user");

            userService.createUser(randomReporter);

            // given an issue created by the random reporter
            Issue issue = issueService.createIssue(issue1, randomReporter, project.getId());

            // copy and update the issue
            String issueJson = mapper.writeValueAsString(issue);
            Issue updatedIssue = mapper.readValue(issueJson, Issue.class);

            updatedIssue.setSummary("updated");
            updatedIssue.setType(IssueType.BUG);

            // given the request body and headers
            HttpEntity<Issue> httpEntity = new HttpEntity<>(updatedIssue, headers);

            // when a put request is made to update an issue that belongs to someone else
            ResponseEntity<ApiError> response = restTemplate.exchange(
                    "/issues/" + issue.getId(),
                    HttpMethod.PUT,
                    httpEntity,
                    ApiError.class
            );

            // then the response should be an unauthorized error
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getErrorMessage()).contains("Forbidden");
        }
    }

    @Nested
    @DisplayName("PATCH")
    class Patch {

        @Test
        public void itShouldUpdateIssueAssignee() {
            // given a user
            User assignee = User.builder()
                    .email("assignee@user")
                    .password("assignee_pass")
                    .fullName("assignee me")
                    .username("issue_assignee")
                    .build();
            assignee = userService.createUser(assignee);

            // given an issue
            issue1 = issueService.createIssue(issue1, authenticatedUser, project.getId());

            // given the request body
            String requestBody = "{\"assignee\" : \"" + assignee.getId() + "\"}";
            HttpEntity httpEntity = new HttpEntity(requestBody, headers);

            // when a PATCH request is made to update the issue assignee
            ResponseEntity<IssueDto> response =
                    restTemplate.exchange("/issues/" + issue1.getId(), HttpMethod.PATCH, httpEntity, IssueDto.class);

            // then the response should be the updated issue
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            assertThat(response.getBody().getAssignee()).isEqualToComparingOnlyGivenFields(assignee);
            assertThat(response.getBody().getReporter()).isEqualToComparingOnlyGivenFields(authenticatedUser);
        }

        @Test
        public void itShouldSwapThePositionsOfTwoIssues() {
            // given the issues are created and belong to the same project
            issue1 = issueService.createIssue(issue1, authenticatedUser, project.getId());
            issue2 = issueService.createIssue(issue2, authenticatedUser, project.getId());

            // given the issues positions
            int issue1Position = issue1.getPosition();
            int issue2Position = issue2.getPosition();

            // given the PATCH endpoint url
            final String url = UriComponentsBuilder
                            .fromUriString("/issues")
                            .queryParam("project", project.getId())
                            .build().toString();

            // given the request body
            final ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("issue1", issue1.getId());
            requestBody.put("issue2", issue2.getId());

            HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(requestBody, headers);

            // when a PATCH request is made to swap the positions of the two issues
            final ResponseEntity<Void> response =
                    restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, Void.class);

            // then expect the issues' positions to have been swapped
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

            issue1 = issueService.getIssueById(issue1.getId());
            issue2 = issueService.getIssueById(issue2.getId());

            assertThat(issue1.getPosition()).isEqualTo(issue2Position);
            assertThat(issue2.getPosition()).isEqualTo(issue1Position);
        }

        @Test
        public void givenSwapIssuesPositions_whenIssuesDoNotBelongToTheSameProject_itShouldNotSwapTheirPositions() {
            // given two distinct projects
            final Project projectPrimary = projectService.createProject(Project.builder().name("Project primary").build(), authenticatedUser);
            final Project projectSecondary = projectService.createProject(Project.builder().name("Project secondary").build(), authenticatedUser);

            // given the two issues belonging to two different projects
            issue1 = issueService.createIssue(issue1, authenticatedUser, projectPrimary.getId());
            issue2 = issueService.createIssue(issue2, authenticatedUser, projectSecondary.getId());

            // given the PATCH endpoint url
            final String url = UriComponentsBuilder
                    .fromUriString("/issues")
                    .queryParam("project", project.getId())
                    .build().toString();

            // given the request body
            final ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("issue1", issue1.getId());
            requestBody.put("issue2", issue2.getId());

            HttpEntity<ObjectNode> httpEntity = new HttpEntity<>(requestBody, headers);

            // when a PATCH request is made to swap the positions of the two issues belonging to two different projects
            final ResponseEntity<ApiError> response =
                    restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, ApiError.class);

            // then expect a forbidden operation error
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Can not swap issues");
        }
    }

    @Nested
    @DisplayName("DELETE")
    class Delete {

        @Test
        public void itShouldDeleteIssueById() {
            // create the issue
            Issue issue = issueService.createIssue(issue1, authenticatedUser, project.getId());

            // set the authorization header
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

            // when a delete request is made
            ResponseEntity<Object> response = restTemplate.exchange(
                    "/issues/" + issue.getId(),
                    HttpMethod.DELETE,
                    httpEntity,
                    Object.class
            );

            // then the issue should have been deleted successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThatExceptionOfType(ResourceNotFoundException.class)
                    .isThrownBy(() -> issueService.getIssueById(issue.getId()))
                    .withMessageContaining("Issue not found");
        }

        @Test
        public void givenDeleteIssue_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
            String errorMessage = "Issue not found";

            // set the authorization header
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

            // when attempting to delete an issue that does not exist
            ResponseEntity<ApiError> response = restTemplate.exchange(
                    "/issues/404",
                    HttpMethod.DELETE,
                    httpEntity,
                    ApiError.class
            );

            // then a 404 issue not found error should be returned
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().getErrorMessage()).contains(errorMessage);
        }

        @Test
        public void whenAuthenticatedUserIsNotTheReporter_itShouldNotAllowDeleteIssue() {
            // given a random reporter
            User randomReporter = new User();
            randomReporter.setEmail("not.the.authenticated.user@email.com");
            randomReporter.setPassword("bla_bla_bla");
            randomReporter.setUsername("random_reporter");
            randomReporter.setFullName("random reporter");

            userService.createUser(randomReporter);

            // given an issue created by the random reporter
            Issue issue = issueService.createIssue(issue1, randomReporter, project.getId());

            // given the authorization header
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

            // when an attempt is made to delete someone else's issue
            ResponseEntity<ApiError> response = restTemplate.exchange(
                    "/issues/" + issue.getId(),
                    HttpMethod.DELETE,
                    httpEntity,
                    ApiError.class
            );

            // then the operation should be forbidden
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getErrorMessage()).contains("Forbidden");
        }
    }

    @AfterEach
    public void tearDown() {
        issueRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @AfterAll
    public void authTeardown() {
        userRepository.deleteAll();
    }
}
