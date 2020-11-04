package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IssueIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    IssueService issueService;


    @Test
    public void itShouldCreateIssue() {
        Issue issue = new Issue();
        ResponseEntity<Issue> response = restTemplate.postForEntity("/issues", issue, Issue.class);

        response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
    }

    @Test
    public void itShouldReturnIssue() {
        Issue issue = issueService.createIssue(new Issue());

        ResponseEntity<Issue> response = restTemplate.getForEntity("/issues/" + issue.getId(), Issue.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(issue.getId());
    }

}
