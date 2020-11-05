package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IssueController.class)
public class IssueControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    IssueService issueService;

    @Test
    public void itShouldCreateIssue() throws Exception {
        Issue issue = new Issue(1L);

        // mock issue service to add new issue
        when(issueService.createIssue(any())).thenReturn(issue);

        // perform a post request and expect the new issue to have been created
        mockMvc.perform(post("/issues"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void itShouldGetIssueById() throws Exception {
        // mock the service to return a new issue
        when(issueService.getIssue(1L)).thenReturn(new Issue(1L));

        // perform get request and expect proper issue to have been returned
        mockMvc.perform(get("/issues/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    public void itShouldGetAllIssues() throws Exception {
        Issue issue1 = new Issue(1L);
        Issue issue2 = new Issue(2L);
        List<Issue> issues = Arrays.asList(issue1, issue2);

        when(issueService.getAllIssues()).thenReturn(issues);

        // perform get request and expect the list of all issues
        mockMvc.perform(get("/issues"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.[*].id").value(containsInAnyOrder(1, 2)));
    }
}
