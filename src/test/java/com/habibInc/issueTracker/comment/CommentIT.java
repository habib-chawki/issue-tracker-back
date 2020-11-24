package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.issue.IssueService;
import com.habibInc.issueTracker.issue.IssueType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    IssueService issueService;

    @Autowired
    IssueRepository issueRepository;

    Issue issue;
    Comment comment, comment2;

    @BeforeEach
    public void setup() {
        // set up an issue
        issue = new Issue();

        issue.setType(IssueType.BUG);
        issue.setSummary("This is an issue");

        // create the issue
        issueService.createIssue(issue);

        // set up a new comment
        comment = new Comment();

        comment.setContent("My comment");
        comment.setOwner("Me");

        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());

        // associate the comment with the issue
        comment.setIssue(issue);

        // set up another comment without an issue
        comment2 = new Comment();

        comment2.setContent("Another comment");
        comment2.setOwner("Jon Doe");

        comment2.setCreationTime(LocalDateTime.now());
        comment2.setUpdateTime(LocalDateTime.now());
    }

    @Test
    public void itShouldCreateComment() {
        // make post request to create a new comment
        ResponseEntity<Comment> response =
                restTemplate.postForEntity("/comments", comment, Comment.class);

        // expect comment to have been created with an autogenerated positive id
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(comment);
    }

    @Test
    public void whenIssueIdIsIncorrect_itShouldReturnIssueNotFoundError() {
        // when a post request is made to add a new comment with an incorrect issue id
        ResponseEntity<ApiError> response =
                restTemplate.postForEntity("/comments", comment2, ApiError.class);

        // then a 404 issue not found error should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Issue not found");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @AfterEach
    public void teardown() {
        issueRepository.deleteAll();
    }
}
