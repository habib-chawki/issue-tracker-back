package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueService;
import com.habibInc.issueTracker.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommentServiceTest {

    // inject mocks into the service
    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    IssueService issueService;

    User owner;
    Issue issue;
    Comment comment;

    @BeforeEach
    public void init() {
        // initialize mocks
        initMocks(this);
    }

    @BeforeEach
    public void setup() {
        // set up an owner
        owner = new User();
        owner.setId(10L);
        owner.setEmail("owner@comment.com");

        // set up an issue
        issue = new Issue();
        issue.setId(100L);
        issue.setSummary("The comment issue");

        // set up a new comment
        comment = new Comment();

        comment.setId(1L);
        comment.setContent("My comment");
        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
    }

    @Test
    public void itShouldCreateComment() {
        // given a call to the repository "save()" method
        when(commentRepository.save(comment)).thenReturn(comment);

        // given a call to the issueService "getIssue()" method
        when(issueService.getIssue(issue.getId())).thenReturn(issue);

        // when the "createComment()" service method is called
        Comment response = commentService.createComment(comment, issue.getId(), owner);

        // then the response should be the comment with the issue and owner both set
        assertThat(response).isEqualTo(comment);
        assertThat(response.getIssue()).isEqualTo(issue);
        assertThat(response.getOwner()).isEqualTo(owner);
    }

    @Test
    public void givenCreateComment_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
        // when the issue does not exist
        when(issueService.getIssue(404L)).thenThrow(ResourceNotFoundException.class);

        // then an issue not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> commentService.createComment(comment, 404L, null));
    }

    @Test
    public void itShouldGetCommentByIssueId() {
        // when commentRepository#findByIssueId is invoked then return the comment
        when(commentRepository.findByIssueId(issue.getId()))
                .thenReturn(Optional.of(comment));

        // when attempting to fetch the comment by its issue id
        Comment returnedComment = commentService.getCommentByIssueId(issue.getId());

        // then the comment should be fetched successfully
        assertThat(returnedComment).isEqualTo(comment);
    }

    @Test
    public void itShouldGetCommentById() {
        // when commentRepository#findById is invoked then return the comment
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        // when attempting to fetch the comment by id
        Comment response = commentService.getCommentById(comment.getId());

        // then the comment should be fetched successfully
        assertThat(response).isEqualTo(comment);
    }

    @Test
    public void givenGetCommentById_whenCommentDoesNotExist_itShouldReturnCommentNotFoundError() {
        // when the comment does not exist
        when(commentRepository.findById(404L)).thenReturn(Optional.ofNullable(null));

        // then a 404 comment not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> commentService.getCommentById(404L))
                .withMessageContaining("Comment not found");
    }

    @Test
    public void givenGetCommentByIssueId_whenCommentDoesNotExist_itShouldReturnCommentNotFoundError() {
        // when the comment does not exist
        when(commentRepository.findByIssueId(404L)).thenReturn(Optional.ofNullable(null));

        // then a 404 comment not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> commentService.getCommentByIssueId(404L))
                .withMessageContaining("Comment not found");
    }

    @Test
    public void itShouldDeleteCommentById() {
        // set the comment owner
        comment.setOwner(owner);

        when(commentRepository.findByIssueId(issue.getId())).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(comment.getId());

        // comment should be deleted successfully
        commentService.deleteComment(issue.getId(), comment.getId(), owner);
    }

    @Test
    public void givenDeleteComment_whenCommentIsNotFoundByIssueId_itShouldReturnCommentNotFoundError() {
        // when the comment cannot be found by issue id (ie. the issue does not exist)
        when(commentRepository.findByIssueId(404L)).thenReturn(Optional.ofNullable(null));

        // then expect a 404 comment not found exception to be raised
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> commentService.deleteComment(404L, comment.getId(), owner))
                .withMessageContaining("Comment not found");
    }

    @Test
    public void givenDeleteComment_whenAuthenticatedUserIsNotTheOwner_itShouldReturnForbiddenError() {
        // given a random authenticated user
        User randomUser = new User();
        randomUser.setId(666L);
        randomUser.setEmail("random.user@email.com");

        // given the comment owner
        comment.setOwner(owner);

        // when users attempt to delete comments that do not belong to them
        when(commentRepository.findByIssueId(issue.getId())).thenReturn(Optional.of(comment));

        // then a forbidden error should be returned
        assertThatExceptionOfType(ForbiddenOperationException.class)
                .isThrownBy(() -> commentService.deleteComment(issue.getId(), comment.getId(), randomUser))
                .withMessageContaining("Forbidden");
    }

    @Test
    public void itShouldUpdateComment(){
        comment.setOwner(owner);
        comment.setIssue(issue);

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        String updatedContent = "This is it, the new content!";

        Comment response =
                commentService.updateComment(comment.getId(), issue.getId(), updatedContent, owner);

        // the comment content should be updated successfully
        assertThat(response).isEqualTo(comment);
        assertThat(response.getContent()).isEqualTo(updatedContent);
    }

    @Test
    public void givenUpdateComment_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
        comment.setOwner(owner);
        comment.setIssue(issue);

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> commentService.updateComment(
                        comment.getId(), 404L, "new content", owner))
                .withMessageContaining("Issue not found");
    }

    @Test
    public void givenUpdateComment_whenAuthenticatedUserIsNotTheOwner_itShouldReturnForbiddenOperationError(){
        fail();
    }
}
