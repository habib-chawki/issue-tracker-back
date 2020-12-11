package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    @BeforeEach
    public void auth() {
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
    public void init() {
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
        issue1.setEstimate(LocalTime.of(2, 0));

        // set up issue2 properties
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setResolution(IssueResolution.DUPLICATE);
        issue2.setAssignee(assignee2);
        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setEstimate(LocalTime.of(6, 15));
    }

    @Test
    public void itShouldCreateIssue() {
        // set up request body and authorization header
        HttpEntity<Issue> httpEntity = new HttpEntity<>(issue1, headers);

        ResponseEntity<Issue> response =
                restTemplate.postForEntity("/issues", httpEntity, Issue.class);

        // expect issue to have been created successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(issue1);
    }

    @Test
    public void itShouldGetIssueById() {
        // save issue2
        issueRepository.save(issue2);

        // set up authorization header
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        // make get request to retrieve an issue by id
        ResponseEntity<Issue> response = restTemplate.exchange(
                "/issues/" + issue2.getId(),
                HttpMethod.GET,
                httpEntity,
                Issue.class
        );

        // expect the proper issue to have been retrieved
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isPositive();
        assertThat(response.getBody()).isEqualTo(issue2);
    }

    @Test
    public void whenIssueCannotBeFoundById_itShouldReturnIssueNotFoundError() {
        // set up authorization header
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        // when a request for an issue that does not exist is received
        ResponseEntity<ApiError> response = restTemplate.exchange(
                "/issues/" + 3L,
                HttpMethod.GET,
                httpEntity,
                ApiError.class
        );

        // then the response should be a 404 error with an 'issue not found' message
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Issue not found");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    public void whenIssueIdIsInvalid_itShouldReturnInvalidIssueIdError() {
        // set up authorization header
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        // when a request with an invalid issue id is received
        ResponseEntity<ApiError> response = restTemplate.exchange(
                "/issues/invalid",
                HttpMethod.GET,
                httpEntity,
                ApiError.class
        );

        // then the response should be a 400 error with an 'invalid issue id' message
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Invalid issue id");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    public void itShouldGetAllIssues() {
        // set up authorization header
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        // given a list of issues
        List<Issue> issues = Arrays.asList(issue1, issue2);
        issueRepository.saveAll(issues);

        // fetch the list of all issues
        ResponseEntity<Issue[]> response = restTemplate.exchange(
                "/issues",
                HttpMethod.GET,
                httpEntity,
                Issue[].class
        );

        // convert the response issues array to list
        List<Issue> responseBody = Arrays.asList(response.getBody());

        // expect all issues to have been retrieved
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).isEqualTo(issues);
    }

    @Test
    public void itShouldSetTheCurrentLoggedInUserAsTheIssueReporter() {
        // set up the request body and the authorization header
        HttpEntity<Issue> httpEntity = new HttpEntity<>(issue1, headers);

        // when a post request to create an issue is received
        ResponseEntity<Issue> response =
                restTemplate.postForEntity("/issues", httpEntity, Issue.class);

        // then the created issue should have the current logged-in user as a reporter
        assertThat(response.getBody().getReporter().getEmail())
                .isEqualTo(authenticatedUser.getEmail());
    }

    @Test
    public void itShouldUpdateIssue() throws JsonProcessingException {
        // create the issue
        Issue issue = issueService.createIssue(issue1, authenticatedUser);

        // copy and update the issue
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
                "/issues/100",
                HttpMethod.PUT,
                httpEntity,
                ApiError.class
        );

        // then the response should be a 404 issue not found error
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Issue not found");
    }

    @Test
    public void givenUpdateIssue_whenIssueIdIsInvalid_itShouldReturnInvalidIssueIdError() {
        // set up the request body and headers
        HttpEntity<Issue> httpEntity = new HttpEntity<>(issue1, headers);

        // when a put request is made with an invalid issue id
        ResponseEntity<ApiError> response = restTemplate.exchange(
                "/issues/invalid",
                HttpMethod.PUT,
                httpEntity,
                ApiError.class
        );

        // then the response should be a 400 bad request error
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Invalid issue id");
    }

    @Test
    public void itShouldNotUpdateIssueIfAuthenticatedUserIsNotTheReporter() throws JsonProcessingException {
        // given a random reporter who's not the authenticated user
        User randomReporter = new User();

        randomReporter.setEmail("not.the.authenticated.user@email.com");
        randomReporter.setPassword("bla_bla_bla");

        userService.createUser(randomReporter);

        // given an issue created by the random reporter
        Issue issue = issueService.createIssue(issue1, randomReporter);

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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getErrorMessage()).contains("Unauthorized");
    }

    @AfterEach
    public void tearDown() {
        issueRepository.deleteAll();
        userRepository.deleteAll();
    }
}
