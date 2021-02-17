package com.habibInc.issueTracker.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class ProjectServiceTest {

    @InjectMocks
    ProjectService projectService;

    @Mock
    ProjectRepository projectRepository;

    @BeforeEach
    public void setup() {

    }

    @Test
    public void itShouldCreateProject() {

    }
}
