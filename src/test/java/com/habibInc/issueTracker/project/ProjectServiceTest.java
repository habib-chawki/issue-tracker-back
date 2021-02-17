package com.habibInc.issueTracker.project;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @InjectMocks
    ProjectService projectService;

    @Mock
    ProjectRepository projectRepository;

    Project project;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        project = Project.builder().id(1L).name("Proj").build();
    }

    @Test
    public void itShouldCreateProject() {
        // given the project repository returns the saved project
        when(projectRepository.save(project)).thenReturn(project);

        // when the project service is invoked to create the project
        Project createdProject = projectService.createProject(project);

        // then expect the project repository to have been invoked
        verify(projectRepository, times(1)).save(project);
        assertThat(createdProject).isEqualTo(project);
    }
}
