package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.issue.IssueType;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    CommentRepository commentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    JwtUtil jwtUtil;

    User authenticatedUser;
    Issue issue;
    Comment comment, comment2;

    String token;
    HttpHeaders headers;

    @BeforeEach
    public void authSetup() {
        // create a user to authenticate
        authenticatedUser = new User();
        authenticatedUser.setEmail("authenticated.user@email.com");
        authenticatedUser.setPassword("my_password");
        authenticatedUser.setUsername("auth_username");
        authenticatedUser.setFullName("auth fullname");

        // save the user to pass authorization
        authenticatedUser = userService.createUser(authenticatedUser);

        // generate an auth token signed with the user email
        token = jwtUtil.generateToken(authenticatedUser.getEmail());

        // set up the authorization header with the auth token
        headers = new HttpHeaders();
        headers.add(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + token);
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

    @Nested
    @DisplayName("POST")
    class Post {

        final String baseUrl = "/issues/%s/comments";

        HttpEntity<Comment> httpEntity;

        @BeforeEach
        public void setup() {
            httpEntity  = new HttpEntity<>(comment, headers);
        }

        @Test
        public void itShouldCreateComment() {
            // given the endpoint url
            String url = String.format(baseUrl, issue.getId());

            // given the expected response
            CommentDto createdComment = modelMapper.map(comment, CommentDto.class);

            // when a POST request is made to create a new comment
            ResponseEntity<CommentDto> response =
                    restTemplate.exchange(url, HttpMethod.POST, httpEntity, CommentDto.class);

            // then expect the comment to have been created successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getId()).isNotNull().isPositive();

            assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(comment);
        }

        @Test
        public void givenCreateComment_itShouldSetTheIssueAndTheAuthenticatedUserAsOwner() {
            // given the endpoint url
            String url = String.format(baseUrl, issue.getId());

            // when a POST request is made to create a comment
            ResponseEntity<Comment> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    Comment.class
            );

            // then the owner and issue of the created comment should be set
            Comment createdComment = commentService.getCommentById(response.getBody().getId());

            assertThat(createdComment.getOwner()).isEqualTo(authenticatedUser);
            assertThat(createdComment.getIssue()).isEqualTo(issue);
        }

        @Test
        public void givenCreateComment_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
            // given an issue that does not exist
            String url = String.format(baseUrl, 404L);

            // when a post request is made to add a new comment with an incorrect issue id
            ResponseEntity<ApiError> response = restTemplate.exchange(
                            url, HttpMethod.POST, httpEntity, ApiError.class
                    );

            // then a 404 issue not found error should be returned
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Issue not found");
            assertThat(response.getBody().getTimestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("DELETE")
    class Delete {

        final String baseUrl = "/issues/%s/comments/%s";

        HttpEntity<Void> httpEntity;

        @BeforeEach
        public void setup() {
            httpEntity = new HttpEntity<>(headers);
        }

        @Test
        public void itShouldDeleteComment() {
            // create the comment
            comment = commentService.createComment(comment, issue.getId(), authenticatedUser);

            // set base url
            String url = String.format(baseUrl, issue.getId().toString(), comment.getId());

            // when a delete request is made
            ResponseEntity<Void> response =
                    restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Void.class);

            // then the comment should be deleted successfully
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThatExceptionOfType(ResourceNotFoundException.class)
                    .isThrownBy(() -> commentService.getCommentById(issue.getId()))
                    .withMessageContaining("Comment not found");
        }

        @Test
        public void givenDeleteComment_whenAuthenticatedUserIsNotTheOwner_itShouldReturnForbiddenError() {
            // set and save a random user
            User randomUser = new User();
            randomUser.setEmail("random@user.me");
            randomUser.setPassword("random_pwd");
            randomUser.setUsername("rand_user");
            randomUser.setFullName("rand user");

            randomUser = userService.createUser(randomUser);

            // create the comment by the random user
            comment = commentService.createComment(comment, issue.getId(), randomUser);

            // set base url
            String url = String.format(baseUrl, issue.getId().toString(), comment.getId());

            // when attempting to delete a comment that does not belong to the authenticated user
            ResponseEntity<ApiError> response =
                    restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, ApiError.class);

            // then a forbidden error should be returned
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Forbidden");

            assertThatExceptionOfType(ForbiddenOperationException.class)
                    .isThrownBy(() -> commentService.deleteComment(issue.getId(), comment.getId(), authenticatedUser))
                    .withMessageContaining("Forbidden");
        }
    }

    @Nested
    @DisplayName("PATCH")
    class Patch {

        final String baseUrl = "/issues/%s/comments/%s";
        final String updatedContent = "updated comment content";
        final String requestBody = String.format("{\"content\" : \"%s\"}", updatedContent);

        HttpEntity<String> httpEntity;

        @BeforeEach
        public void setup() {
            httpEntity = new HttpEntity<>(requestBody, headers);
        }

        @Test
        public void itShouldUpdateComment() {
            // given a comment created by the authenticated user
            comment = commentService.createComment(comment, issue.getId(), authenticatedUser);

            // given the update comment url
            String url = String.format(baseUrl, issue.getId(), comment.getId());

            // when a request to update the comment is made
            ResponseEntity<Comment> response =
                    restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, Comment.class);

            // then the comment content should be updated
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getContent()).isEqualTo(updatedContent);
            assertThat(response).isEqualToComparingOnlyGivenFields(comment);
        }

        @Test
        public void givenUpdateComment_whenAuthenticatedUserIsNotTheOwner_itShouldReturnForbiddenError() {
            // given a random user
            User randomUser = new User();
            randomUser.setId(555L);
            randomUser.setEmail("random.user@email.com");
            randomUser.setPassword("random_pass");
            randomUser.setUsername("rand_user");
            randomUser.setFullName("just random");

            randomUser = userService.createUser(randomUser);

            // given a comment created by the random user
            comment = commentService.createComment(comment, issue.getId(), randomUser);

            // given the update comment url
            String url = String.format(baseUrl, issue.getId(), comment.getId());

            // when a request to update someone else's comment is made
            ResponseEntity<ApiError> response =
                    restTemplate.exchange(url, HttpMethod.PATCH, httpEntity, ApiError.class);

            // then a forbidden error should be returned
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Forbidden");
            assertThat(response.getBody().getTimestamp()).isNotNull();
        }
    }

    @AfterEach
    public void teardown() {
        commentRepository.deleteAll();
        issueRepository.deleteAll();
        userRepository.deleteAll();
    }
}
