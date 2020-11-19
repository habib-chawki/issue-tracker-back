package com.habibInc.issueTracker.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setup(){
        Comment comment = new Comment();
    }

    @Test
    public void itShouldCreateComment(){
    }
}
