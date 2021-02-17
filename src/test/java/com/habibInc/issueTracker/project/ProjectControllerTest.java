package com.habibInc.issueTracker.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

    Project project;

    @BeforeEach
    public void setup() {
        // set up a project entity
        project = Project.builder().id(1L).name("Primary proj").build();
    }

    @Test
    public void itShouldCreateProject() throws Exception {
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
}
