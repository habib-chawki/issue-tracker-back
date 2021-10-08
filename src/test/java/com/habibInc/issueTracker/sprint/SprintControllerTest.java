package com.habibInc.issueTracker.sprint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.issue.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SprintController.class)
@WithMockUser
public class SprintControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    SprintService sprintService;

    @MockBean
    ModelMapper modelMapper;

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
        // given the sprint DTO
        SprintBacklogDto sprintDto = new ModelMapper().map(sprint, SprintBacklogDto.class);

        // given the service response
        when(sprintService.createSprint(any(Long.class), any(Sprint.class))).thenReturn(sprint);
        when(modelMapper.map(sprint, SprintBacklogDto.class)).thenReturn(sprintDto);

        // given the request body
        String requestBody = objectMapper.writeValueAsString(sprint);

        // given the expected response DTO
        String expectedResponse = objectMapper.writeValueAsString(sprintDto);

        // when a POST request is made, then the sprint should be created successfully
        mockMvc.perform(post("/projects/1/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    public void itShouldGetSprintById() throws Exception {
        // given the sprintService#getSprintById response
        when(sprintService.getSprintById(sprint.getId())).thenReturn(sprint);

        // given the expected sprint DTO response
        SprintBoardDto sprintDto = new ModelMapper().map(sprint, SprintBoardDto.class);
        when(modelMapper.map(sprint, SprintBoardDto.class)).thenReturn(sprintDto);
        String expectedResponse = objectMapper.writeValueAsString(sprintDto);

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
        String requestBody = objectMapper.writeValueAsString(issuesIds);

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
        mockMvc.perform(get("/projects/1/sprints?status=active"))
                .andExpect(status().isOk());
    }

    @Test
    public void itShouldUpdateSprintStatus() throws Exception {
        // given the request body
        String requestBody = "{\"newSprintStatus\": \"active\"}";

        // given the sprint service returns the updated sprint
        when(sprintService.updateSprintStatus(sprint.getId(), SprintStatus.ACTIVE)).thenReturn(sprint);

        // when a PATCH request is made to update the sprint status
        // then expect the response to be the updated sprint
        mockMvc.perform(patch("/projects/1/sprints/" + sprint.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void itShouldUpdateIssueSprint() throws Exception {
        // given an issue
        Issue issue = Issue.builder().id(100L).summary("issue").build();

        // given the old and new sprints ids
        String oldSprintId = "10L";
        Long newSprintId = 20L;

        // given the request body
        String requestBody = "{\"newSprintId\": \""+ newSprintId +"\"}";

        doNothing().when(sprintService).updateIssueSprint(oldSprintId, issue.getId(), newSprintId);

        // given the endpoint url
        String url = "/projects/1/sprints/" + oldSprintId + "/issues/" + issue.getId();
        mockMvc.perform(patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    public void itShouldDeleteSprintById() throws Exception {
        // given the project and sprint id
        Long projectId = 100L;

        // given the sprint service response
        doNothing().when(sprintService).deleteSprintById(sprint.getId());

        // given the DELETE endpoint url
        String url = String.format("/projects/%s/sprints/%s", projectId, sprint.getId());

        // when a DELETE request is made to delete a sprint by id
        // then the response should be a 200 OK
        mockMvc.perform(delete(url)).andExpect(status().isOk());
    }
}
