package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.anyLong;
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
    public void itShouldGetIssueById() throws Exception {
        // mock issue service and return new issue
        when(issueService.getIssue(anyLong())).thenReturn(new Issue(1L));

        mockMvc.perform(get("/issues/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("id").value("1"));
    }

    @Test
    public void itShouldCreateIssue() throws Exception {
        mockMvc.perform(post("/issues"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists());
    }
}
