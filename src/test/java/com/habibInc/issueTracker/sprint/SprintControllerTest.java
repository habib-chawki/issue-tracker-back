package com.habibInc.issueTracker.sprint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.issue.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SprintController.class)
@WithMockUser
public class SprintControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    SprintService sprintService;

    Sprint sprint;
    List<Issue> issues;

    @BeforeEach
    public void setup(){
        sprint = Sprint.builder()
                .id(1L)
                .name("First sprint")
                .goal("sprint goal")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(1))
                .build();

        // set up a list of sprint issues
        issues = List.of(
                Issue.builder().id(100L).summary("issue 1").build(),
                Issue.builder().id(200L).summary("issue 2").build(),
                Issue.builder().id(300L).summary("issue 3").build()
        );
    }

    @Test
    public void itShouldCreateSprint() throws Exception {
        // given the service response
        when(sprintService.createSprint(any(Long.class), any(Sprint.class))).thenReturn(sprint);

        // given the request body
        String requestBody = mapper.writeValueAsString(sprint);

        // when a POST request is made, then the sprint should be created successfully
        mockMvc.perform(post("/projects/1/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(requestBody));
    }

    @Test
    public void itShouldGetSprintById() throws Exception {
        // given the sprintService#getSprintById response
        when(sprintService.getSprintById(sprint.getId())).thenReturn(sprint);

        // given the expected response
        String expectedResponse = mapper.writeValueAsString(sprint);

        // when a GET request is made to fetch a sprint by id
        // then the sprint should be retrieved successfully
        mockMvc.perform(get("/projects/1/sprints/"+sprint.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void itShouldSetSprintBacklog() throws Exception {
        // given the list of issues ids
        List<Long> issuesIds = issues.stream().map((issue) -> issue.getId()).collect(Collectors.toList());

        // given the service returns the number of updated issues
        when(sprintService.setSprintBacklog(eq(sprint.getId()), eq(issuesIds))).thenReturn(issues.size());

        // given the request body
        String requestBody = mapper.writeValueAsString(issuesIds);

        // when a PATCH request is made to add a list of issues to the sprint
        // then the sprint backlog should be set successfully
        mockMvc.perform(patch("/projects/1/sprints/"+sprint.getId()+"/backlog")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void itShouldGetSprintsByStatus() throws Exception {
        // given the sprint service returns a list of active sprints
        when(sprintService.getSprintsByStatus(SprintStatus.ACTIVE)).thenReturn(new ArrayList<>());

        // when a GET request is made to fetch a list of sprints by status
        // then expect the sprints with the correct status to have been fetched
        mockMvc.perform(get("/projects/1/sprints?status=ACTIVE"))
                .andExpect(status().isOk());
    }
}
