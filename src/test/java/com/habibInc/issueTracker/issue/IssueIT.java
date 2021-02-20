package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

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
    ObjectMapper mapper;

    User assignee1, assignee2, authenticatedUser;
    Issue issue1, issue2;

    String token;
    HttpHeaders headers;

    @BeforeAll
    public void authSetup() {
        // create a user to authenticate
        authenticatedUser = new User();
        authenticatedUser.setEmail("Habib@email.com");
        authenticatedUser.setPassword("my_password");

        // save the user to pass authorization
        userService.createUser(authenticatedUser);

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

        // create assignees
        assignee1 = new User();
        assignee2 = new User();

        // set up issue1 properties
        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");
        issue1.setType(IssueType.STORY);
        issue1.setResolution(IssueResolution.DONE);
        issue1.setAssignee(assignee1);
        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setEstimate("4");

        // set up issue2 properties
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setResolution(IssueResolution.DUPLICATE);
        issue2.setAssignee(assignee2);
        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setEstimate("6");
    }

    @Nested
    @DisplayName("POST")
    class Post {
        HttpEntity<Issue> httpEntity;

        @BeforeEach
        public void setup() {
            httpEntity = new HttpEntity<>(issue1, headers);
        }

        @Test
        @DisplayName("Create issue")
        public void itShouldCreateIssue() {
            // when a POST request to create an issue is received
            ResponseEntity<Issue> response =
                    restTemplate.postForEntity("/issues", httpEntity, Issue.class);

            // then expect the issue to have been created successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getId()).isNotNull().isPositive();
            assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(issue1);
        }

        @Test
        @DisplayName("Set the authenticated user as issue reporter")
        public void whenIssueIsCreated_itShouldSetTheAuthenticatedUserAsReporter() {
            // when an issue is created after a POST request
            ResponseEntity<Issue> response =
                    restTemplate.postForEntity("/issues", httpEntity, Issue.class);

            // then the created issue's reporter should be the authenticated user
            assertThat(issueService.getIssueById(response.getBody().getId()).getReporter())
                    .isEqualTo(authenticatedUser);
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
            issue2 = issueService.createIssue(issue2, authenticatedUser, null);

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
        }

        @Test
        public void whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
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
            // create the issue
            Issue issue = issueService.createIssue(issue1, authenticatedUser, null);

            // set up an updated issue
            String issueJson = mapper.writeValueAsString(issue);
            Issue updatedIssue = mapper.readValue(issueJson, Issue.class);

            updatedIssue.setSummary("updated");
            updatedIssue.setType(IssueType.BUG);

            // set up the request body and headers
            HttpEntity<Issue> httpEntity = new HttpEntity<>(updatedIssue, headers);

            // when a put request is made with a valid id of an issue that exists
            ResponseEntity<Issue> response = restTemplate.exchange(
                    "/issues/" + issue.getId(),
                    HttpMethod.PUT,
                    httpEntity,
                    Issue.class
            );

            // then the issue should be updated
            assertThat(issueRepository.findById(issue.getId()).get()).isEqualTo(updatedIssue);

            // the response body should be the updated issue
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(updatedIssue);
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

            randomReporter.setEmail("not.the.authenticated.user@email.com");
            randomReporter.setPassword("bla_bla_bla");

            userService.createUser(randomReporter);

            // given an issue created by the random reporter
            Issue issue = issueService.createIssue(issue1, randomReporter, null);

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
    @DisplayName("DELETE")
    class Delete {

        @Test
        public void itShouldDeleteIssueById() {
            // create the issue
            Issue issue = issueService.createIssue(issue1, authenticatedUser, null);

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

            userService.createUser(randomReporter);

            // given an issue created by the random reporter
            Issue issue = issueService.createIssue(issue1, randomReporter, null);

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
    }

    @AfterAll
    public void authTeardown() {
        userRepository.deleteAll();
    }
}
