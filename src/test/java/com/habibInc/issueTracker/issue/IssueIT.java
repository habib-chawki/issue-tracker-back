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

    @Test
    public void itShouldReturnIssue() {
        ResponseEntity<Issue> response = restTemplate.getForEntity("/issues/1", Issue.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(1);
    }

    @Test
    public void itShouldCreateIssue() {
        Issue newIssue = new Issue();
        ResponseEntity<Issue> response = restTemplate.postForEntity("/issues", newIssue, Issue.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
