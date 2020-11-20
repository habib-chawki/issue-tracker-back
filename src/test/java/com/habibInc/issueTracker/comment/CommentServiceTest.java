package com.habibInc.issueTracker.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommentServiceTest {

    // inject mocks into the service
    @InjectMocks
    CommentService commentService;

    // mock the repository
    @Mock
    CommentRepository commentRepository;

    Comment comment;

    @BeforeEach
    public void init() {
        // initialize mocks
        initMocks(this);
    }

    @BeforeEach
    public void setup() {
        // set up a new comment
        comment = new Comment();

        comment.setContent("My comment");
        comment.setOwner("Me");

        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
    }

    @Test
    public void itShouldCreateComment() {
        // given a call to the repository "save()" method
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // when the "createComment()" service method is called
        Comment response = commentService.createComment(comment);

        // then the response should be the comment itself
        assertThat(response).isEqualTo(comment);
    }
}
