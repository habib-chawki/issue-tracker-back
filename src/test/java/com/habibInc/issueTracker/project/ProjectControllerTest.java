package com.habibInc.issueTracker.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@WithMockUser
public class ProjectControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ProjectService projectService;

    Project project, project2;

    @BeforeEach
    public void setup() {
        // set up project entities
        project = Project.builder().id(1L).name("Primary proj").build();
        project2 = Project.builder().id(2L).name("Secondary proj").build();
    }

    @Test
    public void itShouldCreateProject() throws Exception {
        // given the project service
        when(projectService.createProject(eq(project), any())).thenReturn(project);

        // given the request body
        String requestBody = mapper.writeValueAsString(project);

        // when a POST request is made to create a new project
        // then expect the response to be the created project with a 201 status
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody));
    }

    @Test
    public void itShouldGetListOfProjects() throws Exception {
        // given a list of projects
        List<Project> projects = List.of(this.project, project2);
        when(projectService.getProjects()).thenReturn(projects);

        // given the expected response
        String responseBody = mapper.writeValueAsString(projects);

        // when a GET request is made, then expect the list of projects to be fetched
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @Test
    public void itShouldGetProjectById() throws Exception {
        when(projectService.getProjectById(project.getId())).thenReturn(project);

        // given the expected response
        String responseBody = mapper.writeValueAsString(project);

        mockMvc.perform(get("/projects/" + project.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @Test
    public void givenGetProjectById_whenIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        // when a GET request is made with an invalid project id
        // then a 400 bad request error should be returned
        mockMvc.perform(get("/projects/invalid_id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(Utils.errorMessage));
    }

    @Test
    public void itShouldGetProjectBacklog() throws Exception {
        // given the project backlog
        List<Issue> backlog = List.of(
                Issue.builder().id(100L).summary("issue 1").build(),
                Issue.builder().id(200L).summary("issue 2").build(),
                Issue.builder().id(300L).summary("issue 2").build()
        );

        // given the expected response
        String response = mapper.writeValueAsString(backlog);

        when(projectService.getBacklog(project.getId())).thenReturn(backlog);

        // expect the backlog to be fetched successfully
        mockMvc.perform(get("/projects/" + project.getId() + "/backlog"))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

    @Test
    public void givenGetProjectBacklog_whenProjectIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        mockMvc.perform(get("/projects/invalid_id/backlog"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value(Utils.errorMessage));
    }

    @Test
    public void itShouldGetProjectsByAssignedUser() throws Exception {
        // given a user id
        Long userId = 100L;

        // given a set of projects
        Set<Project> projects = Set.of(
                Project.builder().id(1L).name("Project 01").build(),
                Project.builder().id(2L).name("Project 02").build(),
                Project.builder().id(3L).name("Project 03").build()
        );

        // given the project service response
        when(projectService.getProjectsByAssignedUser(userId)).thenReturn(projects);

        mockMvc.perform(get("/projects?user=" + userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(projects)));
    }

    @Test
    public void itShouldAddUserToProject() throws Exception {
        // given a user id
        Long userId = 100L;

        // given the service response
        doNothing().when(projectService).addUserToProject(userId, project.getId());

        // when a POST request id made to add the user to the project
        // then the response should be a 200 OK
        mockMvc.perform(post("/projects/" + project.getId() + "/users/" + userId))
                .andExpect(status().isOk());

        verify(projectService, times(1)).addUserToProject(userId, project.getId());
    }

    @Test
    public void itShouldRemoveUserFromProject() throws Exception {
        // given a user id
        Long userId = 100L;

        // when a DELETE request is made to remove a user from the project
        // then expect a 200 OK response
        mockMvc.perform(delete("/projects/" + project.getId() + "/users/" + userId))
                .andExpect(status().isOk());
    }
}
