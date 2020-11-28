package com.habibInc.issueTracker.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CommentService commentService;

    Comment comment;

    @BeforeEach
    public void setup() {
        // set up a new comment
        comment = new Comment();

        comment.setId(1L);
        comment.setContent("This is a comment");
        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
    }

    @Test
    public void itShouldCreateComment() throws Exception {
        when(commentService.createComment(comment, 1L)).thenReturn(comment);

        // set up base url and request body
        String baseUrl = String.format("/issues/%s/comments", 1);
        String requestBody = mapper.writeValueAsString(comment);

        // send a post request and expect the comment to be created successfully
        mockMvc.perform(post(baseUrl)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody));
    }

    @Test
    public void itShouldReturnIssueNotFoundError() throws Exception {
        // given that the issue does not exist
        comment.setIssue(null);

        // given the base url and request body
        String baseUrl = String.format("/issues/%s/comments", 10);
        String requestBody = mapper.writeValueAsString(comment);

        // given an error message
        String errorMessage = "Issue not found";

        // when the comment service is invoked to create the comment
        when(commentService.createComment(comment, 10L))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        // then a 404 "issue not found" error should be returned
        mockMvc.perform(post(baseUrl)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void itShouldReturnInvalidIssueIdError() throws Exception {
        // given the request body and an error message
        String requestBody = mapper.writeValueAsString(comment);
        String errorMessage = "Invalid issue id";

        // when the request is made with an invalid issue id
        String baseUrl = String.format("/issues/%s/comments", "invalid_id");

        // then a 404 "Invalid issue id" error should be returned
        mockMvc.perform(post(baseUrl)
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }
}
