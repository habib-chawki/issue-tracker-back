package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.exceptionhandler.ApiError;
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

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IssueIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    IssueService issueService;

    @Autowired
    IssueRepository issueRepository;

    Issue issue1, issue2;

    @BeforeEach
    public void init() {
        // create issue
        issue1 = new Issue();
        issue2 = new Issue();

        // set up issue1 properties
        issue1.setId(1L);
        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");
        issue1.setType(IssueType.STORY);
        issue1.setResolution(IssueResolution.DONE);
        issue1.setAssignee("Me");
        issue1.setReporter("Jon Doe");
        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setEstimate(LocalTime.of(2, 0));

        // set up issue2 properties
        issue2.setId(2L);
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setResolution(IssueResolution.DUPLICATE);
        issue2.setAssignee("You");
        issue2.setReporter("Jane Doe");
        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setEstimate(LocalTime.of(6, 15));
    }

    @Test
    public void itShouldCreateIssue() {
        // make post request to create new issue
        ResponseEntity<Issue> response = restTemplate.postForEntity("/issues", issue1, Issue.class);

        // expect issue to have been created successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(issue1);
    }

    @Test
    public void itShouldGetIssueById() {
        issueService.createIssue(issue2);

        // make get request to retrieve an issue by id
        ResponseEntity<Issue> response =
                restTemplate.getForEntity("/issues/" + issue2.getId(), Issue.class);

        // expect the proper issue to have been retrieved
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isPositive();
        assertThat(response.getBody()).isEqualTo(issue2);
    }

    @Test
    public void itShouldReturnIssueNotFoundError() {
        // when a request for an issue that does not exist is received
        ResponseEntity<ApiError> response =
                restTemplate.getForEntity("/issues/" + 3L, ApiError.class);

        // then the response should be an error with a 404 status and an issue not found message
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Issue not found");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    public void itShouldGetAllIssues() {
        // given a list of issues
        List<Issue> issues = Arrays.asList(issue1, issue2);

        issueRepository.saveAll(issues);

        // fetch the list of all issues
        ResponseEntity<Issue[]> response = restTemplate.getForEntity("/issues", Issue[].class);

        // convert the response issues array to list
        List<Issue> responseBody = Arrays.asList(response.getBody());

        // expect all issues to have been retrieved
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).isEqualTo(issues);
    }

    @AfterEach
    public void tearDown(){
        issueRepository.deleteAll();
    }
}
