package com.habibInc.issueTracker.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@WithMockUser
public class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CommentService commentService;

    Comment comment;
    Issue issue;

    @BeforeEach
    public void setup() {
        // set up a new comment
        comment = new Comment();

        comment.setId(1L);
        comment.setContent("This is a comment");
        comment.setCreationTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());

        // set the comment issue
        issue = new Issue();
        issue.setId(100L);

        comment.setIssue(issue);
    }

    @Test
    public void itShouldCreateComment() throws Exception {
        when(commentService.createComment(eq(comment), eq(100L), any()))
                .thenReturn(comment);

        // set up base url and request body
        String baseUrl = String.format("/issues/%s/comments", 100);
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
        String baseUrl = String.format("/issues/%s/comments", 404);
        String requestBody = mapper.writeValueAsString(comment);

        // given an error message
        String errorMessage = "Issue not found";

        // when the comment service is invoked to create the comment
        when(commentService.createComment(eq(comment), eq(404L), any()))
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

    @Test
    public void itShouldDeleteCommentById() throws Exception {
        String baseUrl = String.format("/issues/%s/comments/%s", 100L, 1L);

        doNothing().when(commentService).deleteComment();

        mockMvc.perform(delete(baseUrl)).andExpect(status().isOk());
    }

    @Test
    public void givenDeleteCommentById_whenIssueOrCommentDoNotExist_itShouldReturnResourceNotFoundError(){
        doThrow(new ResourceNotFoundException("Resource not found"))
                .when(commentService).deleteComment();


    }

    @Test
    public void givenDeleteCommentById_whenIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        // when comment id is invalid
        String baseUrl = String.format("/issues/%s/comments/%s", 100L, "invalid");

        // then a 400 invalid id error should be returned
        mockMvc.perform(delete(baseUrl))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid id"));

        // when the issue id is invalid
        baseUrl = String.format("/issues/%s/comments/%s", "invalid", 1L);

        // then a 400 invalid id error should be returned
        mockMvc.perform(delete(baseUrl))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid id"));

    }
}
