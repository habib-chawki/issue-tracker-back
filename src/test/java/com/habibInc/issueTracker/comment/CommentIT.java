package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.issue.IssueService;
import com.habibInc.issueTracker.issue.IssueType;
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

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    JwtUtil jwtUtil;

    User authenticatedUser;
    Issue issue;
    Comment comment, comment2;

    String token;
    HttpHeaders headers;

    @BeforeEach
    public void auth() {
        // create a user to authenticate
        authenticatedUser = new User();
        authenticatedUser.setEmail("authenticated.user@email.com");
        authenticatedUser.setPassword("my_password");

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
        // set up an issue and create it
        issue = new Issue();

        issue.setSummary("This is an issue");
        issue.setType(IssueType.BUG);

        // save the issue
        issue = issueRepository.save(issue);

        // set up a comment
        comment = new Comment();

        comment.setContent("My comment");
        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());

        // set up another comment without an issue
        comment2 = new Comment();

        comment2.setContent("Another comment");
        comment2.setCreationTime(LocalDateTime.now());
        comment2.setUpdateTime(LocalDateTime.now());
    }

    @Test
    public void itShouldCreateComment() {
        // set up request body and authorization header
        HttpEntity<Comment> httpEntity = new HttpEntity<>(comment, headers);

        String baseUrl = String.format("/issues/%s/comments", issue.getId().toString());

        // make post request to create a new comment
        ResponseEntity<Comment> response =
                restTemplate.postForEntity(baseUrl, httpEntity, Comment.class);

        // expect comment to have been created with an autogenerated positive id
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(comment);
    }

    @Test
    public void givenCreateComment_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
        // set up request body and authorization header
        HttpEntity<Comment> httpEntity = new HttpEntity<>(comment2, headers);

        // given an issue that does not exist
        String baseUrl = String.format("/issues/%s/comments", 10L);

        // when a post request is made to add a new comment with an incorrect issue id
        ResponseEntity<ApiError> response =
                restTemplate.postForEntity(baseUrl, httpEntity, ApiError.class);

        // then a 404 'issue not found' error should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Issue not found");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    public void givenCreateComment_whenIssueIdIsInvalid_itShouldReturnInvalidIssueIdError() {
        // set up request body and authorization header
        HttpEntity<Comment> httpEntity = new HttpEntity<>(comment, headers);

        // given an issue with an invalid id
        String baseUrl = "/issues/invalid/comments";

        // when a post request is received with an invalid issue id
        ResponseEntity<ApiError> response =
                restTemplate.postForEntity(baseUrl, httpEntity, ApiError.class);

        // then a 400 'invalid issue id' error should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Invalid issue id");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    public void givenCreateComment_itShouldSetTheIssueAndTheAuthenticatedUserAsOwner() {
        // set up request body and authorization header
        HttpEntity<Comment> httpEntity = new HttpEntity<>(comment, headers);

        // set up base url
        String baseUrl = String.format("/issues/%s/comments", issue.getId().toString());

        // when a post request is made to create a new comment
        ResponseEntity<Comment> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                httpEntity,
                Comment.class
        );

        // then the created comment's owner should be the currently logged-in user
        assertThat(response.getBody().getOwner()).isEqualTo(authenticatedUser);

        // the issue should be set
        assertThat(response.getBody().getIssue()).isEqualTo(issue);
    }

    @Test
    public void itShouldDeleteComment() {
        // set authorization header
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        // create the comment
        comment = commentService.createComment(comment, issue.getId(), authenticatedUser);

        // set base url
        String baseUrl = String.format("/issues/%s/comments/%s", issue.getId().toString(), comment.getId());

        // when a delete request is made
        ResponseEntity<Object> response =
                restTemplate.exchange(baseUrl, HttpMethod.DELETE, httpEntity, Object.class);

        // then the comment should be deleted successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> commentService.getCommentByIssueId(issue.getId()))
                .withMessageContaining("Comment not found");
    }

    @Test
    public void givenDeleteComment_whenAuthenticatedUserIsNotTheOwner_itShouldReturnForbiddenError() {

    }

    @AfterEach
    public void teardown() {
        issueRepository.deleteAll();
        userRepository.deleteAll();
    }
}
