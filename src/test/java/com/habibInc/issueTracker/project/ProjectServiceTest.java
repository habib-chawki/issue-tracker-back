package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @InjectMocks
    ProjectService projectService;

    @Mock
    ProjectRepository projectRepository;

    Project project, project2;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        project = Project.builder().id(1L).name("Proj").build();
        project2 = Project.builder().id(2L).name("Secondary proj").build();
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

    @Test
    public void itShouldGetListOfProjects() {
        // given the repository returns a list of projects
        List<Project> projects = List.of(this.project, project2);
        when(projectRepository.findAll()).thenReturn(projects);

        // when the service is invoked to fetch the list of projects
        List<Project> retrievedProjects = projectService.getProjects();

        // then expect the list of projects to have been fetched successfully
        verify(projectRepository, times(1)).findAll();
        assertThat(retrievedProjects).isEqualTo(projects);
    }

    @Test
    public void itShouldGetProjectById() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        Project retrievedProject = projectService.getProjectById(project.getId());

        // expect the project to have been retrieved by id successfully
        verify(projectRepository, times(1)).findById(project.getId());
        assertThat(retrievedProject).isEqualTo(project);
    }

    @Test
    public void givenGetProjectById_whenProjectDoesNotExist_itShouldReturnProjectNotFoundError() {
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> projectService.getProjectById(404L));
    }
}
