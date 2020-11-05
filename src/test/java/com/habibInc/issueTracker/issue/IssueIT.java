package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

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

    @Test
    public void itShouldCreateIssue() {
        Issue issue = new Issue();

        // make post request to create new issue
        ResponseEntity<Issue> response = restTemplate.postForEntity("/issues", issue, Issue.class);

        // expect issue to have been created successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
    }

    @Test
    public void itShouldGetIssueById() {
        Issue issue = issueService.createIssue(new Issue());

        // make get request to retrieve an issue by id
        ResponseEntity<Issue> response = restTemplate.getForEntity("/issues/" + issue.getId(), Issue.class);

        // expect the proper issue to have been retrieved
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(issue.getId());
    }

    @Test
    public void itShouldGetAllIssues() {
        // save a list of issues
        Issue issue1 = new Issue();
        Issue issue2 = new Issue();
        Issue issue3 = new Issue();
        List<Issue> issues = Arrays.asList(issue1, issue2, issue3);

        issueRepository.saveAll(issues);

        // make a get request to fetch a list of all issues
        ResponseEntity<Issue[]> response = restTemplate.getForEntity("/issues", Issue[].class);

        // convert the response issues array to list
        List<Issue> returnedIssues = Arrays.asList(response.getBody());

        // expect all issues to have been retrieved
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(returnedIssues).isEqualTo(issues);
    }

    @AfterEach
    public void teardown(){
        // delete all issues
        issueRepository.deleteAll();
    }

}
