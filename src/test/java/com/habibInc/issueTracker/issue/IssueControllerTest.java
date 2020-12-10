package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IssueController.class)
@WithMockUser
public class IssueControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    IssueService issueService;

    @MockBean
    UserService userService;

    Issue issue1, issue2;

    @BeforeEach
    public void init() {
        // create issue
        issue1 = new Issue();
        issue2 = new Issue();

        // set up issue1 properties
        issue1.setId(1L);
        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");
        issue1.setType(IssueType.STORY);
        issue1.setResolution(IssueResolution.DONE);
        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setEstimate(LocalTime.of(2, 0));

        // set up issue2 properties
        issue2.setId(2L);
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setResolution(IssueResolution.DUPLICATE);
        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setEstimate(LocalTime.of(6, 15));
    }

    @Test
    public void itShouldCreateIssue() throws Exception {

        // mock issue service to add new issue
        when(issueService.createIssue(eq(issue1), any())).thenReturn(issue1);

        // set up json request body
        String requestBody = mapper.writeValueAsString(issue1);

        // perform a post request and expect the new issue to have been created
        mockMvc.perform(post("/issues")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody));
    }

    @Test
    public void itShouldGetIssueById() throws Exception {
        // return an issue when getIssue service method is invoked
        when(issueService.getIssue(2L)).thenReturn(issue2);

        // the response body is expected to contain the returned issue
        String responseBody = mapper.writeValueAsString(issue2);

        // expect the proper issue to have been returned when a get request is made
        mockMvc.perform(get("/issues/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseBody));
    }

    @Test
    public void whenIssueCannotBeFoundById_itShouldReturnIssueNotFoundError() throws Exception {
        // given an error message
        String errorMessage = "Issue not found";

        // when the "getIssue()" service method throws a resource not found exception
        when(issueService.getIssue(3L))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        // then an error message with a status code of 404 should be returned
        mockMvc.perform(get("/issues/3"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void whenIssueIdIsInvalid_itShouldReturnInvalidIssueIdError() throws Exception {
        String errorMessage = "Invalid issue id";

        // expect an invalid issue id error
        mockMvc.perform(get("/issues/invalid"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void itShouldGetAllIssues() throws Exception {
        // given a list of issues
        List<Issue> issues = Arrays.asList(issue1, issue2);

        // when issue service is invoked, return the list of issues
        when(issueService.getAllIssues()).thenReturn(issues);

        // perform get request and expect the list of all issues to have been returned
        mockMvc.perform(get("/issues"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(mapper.writeValueAsString(issues)));
    }

    @Test
    public void itShouldUpdateIssue() throws Exception {
        when(issueService.updateIssue(issue1)).thenReturn(issue2);

        String requestBody = mapper.writeValueAsString(issue1);
        String expectedResponse = mapper.writeValueAsString(issue2);

        mockMvc.perform(put("/issues")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponse));
    }
}
