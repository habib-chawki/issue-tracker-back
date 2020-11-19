package com.habibInc.issueTracker.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    Comment comment;

    @BeforeEach
    public void setup(){
        // set up a new comment
        comment = new Comment();

        comment.setOwner("owner");
        comment.setContent("This is a comment");

        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
    }

    @Test
    public void itShouldCreateComment(){
        when(commentService.createComment(comment)).thenReturn(comment);
    }
}
