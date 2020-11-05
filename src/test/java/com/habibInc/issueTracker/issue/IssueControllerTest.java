package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
                .andExpect(jsonPath("id").exists());
    }

    @Test
    public void itShouldGetIssueById() throws Exception {
        // mock the service to return a new issue
        when(issueService.getIssue(1L)).thenReturn(new Issue(1L));

        // perform get request and expect proper issue to have been returned
        mockMvc.perform(get("/issues/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("id").value("1"));
    }
}
