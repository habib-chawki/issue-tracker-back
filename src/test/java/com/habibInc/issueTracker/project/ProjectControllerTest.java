package com.habibInc.issueTracker.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {
    @Autowired
    MockMvc mockMvc;

    Project project;

    public void setup() {
        // set up a project entity
        project = Project.builder().id(1L).name("Primary proj").build();
    }

    public void itShouldCreateProject() {

    }
}
