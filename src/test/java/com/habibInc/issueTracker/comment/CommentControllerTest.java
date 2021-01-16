package com.habibInc.issueTracker.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.user.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        comment = Comment.builder()
                .id(1L)
                .content("This is a comment")
                .creationTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // set the comment issue
        issue = new Issue();
        issue.setId(100L);

        comment.setIssue(issue);
    }

    @Test
    public void itShouldCreateComment() throws Exception {
        when(commentService.createComment(eq(comment), eq(issue.getId()), any()))
                .thenReturn(comment);

        // set up base url and request body
        String baseUrl = String.format("/issues/%s/comments", issue.getId());
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
    public void givenCreateComment_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() throws Exception {
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
    public void givenCreateComment_whenIssueIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
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
        // given a comment to delete
        String baseUrl = String.format("/issues/%s/comments/%s", issue.getId(), comment.getId());

        // when deleteComment service method is invoked
        doNothing().when(commentService).deleteComment(eq(issue.getId()), eq(comment.getId()), any());

        // then the comment should be deleted successfully
        mockMvc.perform(delete(baseUrl)).andExpect(status().isOk());
    }

    @Test
    public void givenDeleteCommentById_whenCommentDoesNotExist_itShouldReturnCommentNotFoundError() throws Exception {
        String baseUrl = String.format("/issues/%s/comments/%s", issue.getId(), 404L);
        String errorMessage = "Comment not found";

        // when attempting to delete a comment that does not exist
        doThrow(new ResourceNotFoundException(errorMessage))
                .when(commentService).deleteComment(eq(issue.getId()), eq(404L), any());

        // then a 404 error should be returned
        mockMvc.perform(delete(baseUrl))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void givenDeleteCommentById_whenIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        // when comment id is invalid
        String baseUrl = String.format("/issues/%s/comments/%s", 100L, "invalid_comment_id");

        // then a 400 invalid id error should be returned
        mockMvc.perform(delete(baseUrl))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid id"));

        // when issue id is invalid
        baseUrl = String.format("/issues/%s/comments/%s", "invalid_issue_id", 1L);

        // then a 400 invalid id error should be returned
        mockMvc.perform(delete(baseUrl))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid id"));
    }

    @Test
    public void itShouldUpdateCommentById() throws Exception {
        String baseUrl =
                String.format("/issues/%s/comments/%s", issue.getId(), comment.getId());

        // given the updated comment content
        String requestBody = "{\"content\": \"updated comment content\"}";

        // when comment service is invoked to update the comment
        when(commentService.updateComment(
                eq(comment.getId()), eq(issue.getId()), anyString(), any(User.class))
        ).thenReturn(comment);

        // then the update should be successful
        mockMvc.perform(patch(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void givenUpdateCommentById_whenIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        String errorMessage = "Invalid id";
        String newCommentContent = "{\"content\": \"updated comment content\"}";

        // when the issue id is invalid
        String baseUrl = String.format("/issues/%s/comments/%s", "invalid_issue_id", comment.getId());

        // then a 400 invalid id error should be returned
        mockMvc.perform(patch(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCommentContent))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));

        // when the comment id is invalid
        baseUrl = String.format("/issues/%s/comments/%s", issue.getId(), "invalid_comment_id");

        // then a 400 invalid id error should be returned
        mockMvc.perform(patch(baseUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCommentContent))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }
}
