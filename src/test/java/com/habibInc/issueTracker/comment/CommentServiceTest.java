package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

    Issue issue;
    Comment comment;

    @BeforeEach
    public void init() {
        // initialize mocks
        initMocks(this);
    }

    @BeforeEach
    public void setup() {
        // set up an issue
        issue = new Issue();
        issue.setId(100L);

        // set up a new comment
        comment = new Comment();

        comment.setId(1L);
        comment.setContent("My comment");
        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setIssue(issue);
    }

    @Test
    public void itShouldCreateComment() {
        // given a call to the repository "save()" method
        when(commentRepository.save(comment)).thenReturn(comment);

        // given a call to the issueService "getIssue()" method
        when(issueService.getIssue(issue.getId())).thenReturn(issue);

        // when the "createComment()" service method is called
        Comment response = commentService.createComment(comment, issue.getId(), null);

        // then the response should be the comment itself
        assertThat(response).isEqualTo(comment);
    }

    @Test
    public void itShouldReturnIssueNotFoundError() {
        // when the issue does not exist
        when(issueService.getIssue(404L)).thenReturn(null);

        // then an issue not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> commentService.createComment(comment, 404L, null));
    }
}
