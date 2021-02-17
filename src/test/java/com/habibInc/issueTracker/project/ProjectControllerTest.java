package com.habibInc.issueTracker.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        when(projectService.createProject(project)).thenReturn(project);

        // given the request body
        String requestBody = mapper.writeValueAsString(project);

        // when a POST request is made to create a new project
        // then expect the response to be the created project with a 201 status
        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
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
}
