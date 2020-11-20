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

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    Comment comment;

    @BeforeEach
    public void setup(){
        // initialize mocks
        initMocks(this);

        comment = new Comment();

        comment.setContent("My comment");
        comment.setOwner("Me");

        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
    }


    @Test
    public void itShouldCreateComment(){
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment response = commentService.createComment(comment);

        assertThat(response).isEqualTo(comment);
    }
}
