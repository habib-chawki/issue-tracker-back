package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    @InjectMocks
    ProjectService projectService;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    IssueRepository issueRepository;

    @Mock
    UserService userService;

    Project project, project2;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        project = Project.builder().id(1L).name("Primary project").build();
        project2 = Project.builder().id(2L).name("Secondary project").build();
    }

    @Test
    public void itShouldCreateProject() {
        // given the authenticated user
        User authenticatedUser = User.builder().id(777L).email("authenticated@user").password("auth_pass").build();

        // given the project repository returns the saved project
        when(projectRepository.save(project)).thenReturn(project);

        // when the project service is invoked to create the project
        Project createdProject = projectService.createProject(project, authenticatedUser);

        // then expect the project repository to have been invoked
        verify(projectRepository, times(1)).save(project);
        assertThat(createdProject).isEqualTo(project);
    }

    @Test
    public void givenCreateProject_itShouldSetAuthenticatedUserAsProjectOwner() {
        // given the authenticated user
        User authenticatedUser =
                User.builder().id(555L).email("user@email.com").password("userPass").build();

        when(projectRepository.save(project)).thenReturn(project);

        // when the project is created
        Project createdProject = projectService.createProject(project, authenticatedUser);

        // then the authenticated user should be set as project owner
        assertThat(createdProject.getOwner()).isEqualTo(authenticatedUser);
    }

    @Test
    public void givenCreateProject_itShouldAddAuthenticatedUserToAssignedUsers() {
        // given the authenticated user
        User authenticatedUser = User.builder().id(555L).email("authenticated@user").password("auth_pass").build();

        // given the repository response
        when(projectRepository.save(project)).thenReturn(project);

        // when the project is created
        Project createdProject = projectService.createProject(project, authenticatedUser);

        // then the authenticated user should be added to the set of assigned users
        assertThat(createdProject.getAssignedUsers()).containsExactly(authenticatedUser);
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

    @Test
    public void itShouldGetProjectBacklog() {
        // given the project backlog
        List<Issue> backlog = List.of(
                Issue.builder().id(100L).summary("issue 1").build(),
                Issue.builder().id(200L).summary("issue 2").build(),
                Issue.builder().id(300L).summary("issue 2").build()
        );

        when(issueRepository.findAllByProjectIdAndSprintId(project.getId(), null)).thenReturn(backlog);

        // when projectService is invoked to get the project backlog
        List<Issue> retrievedBacklog = projectService.getBacklog(project.getId());

        // then the backlog should be retrieved successfully
        assertThat(retrievedBacklog).isEqualTo(backlog);
    }

    @Test
    public void itShouldGetProjectsByAssignedUser() {
        // given a user id
        Long userId = 10L;

        // given a set of projects
        Set<Project> projects = Set.of(
                Project.builder().id(1L).name("Project 01").build(),
                Project.builder().id(2L).name("Project 02").build(),
                Project.builder().id(3L).name("Project 03").build()
        );

        // given the repository response
        when(projectRepository.findAllByAssignedUsersId(10L)).thenReturn(projects);

        // when the service is invoked to get the projects by assigned user
        Set<Project> projectsByAssignedUser = projectService.getProjectsByAssignedUser(userId);

        // then expect the projects to have been fetched successfully
        assertThat(projectsByAssignedUser).isEqualTo(projects);

        verify(projectRepository, times(1)).findAllByAssignedUsersId(userId);
    }

    @Test
    public void itShouldAddUserToProject() {
        // given a user
        User user = User.builder().id(888L).email("user@email").password("user_pass").build();

        // given
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        // when addUserToProject() is invoked
        projectService.addUserToProject(user.getId(), project.getId());

        // then the the user should be added successfully via the repository
        verify(projectRepository).addUserToProject(user.getId(), project.getId());
    }

    @Test
    public void itShouldRemoveUserFromProject() {
        // given a user
        User user = User.builder().id(888L).email("user@email").password("user_pass").build();

        // given
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        // when removeUserFromProject() is invoked
        projectService.removeUserFromProject(user.getId(), project.getId());

        // then the user should be remove successfully
        verify(projectRepository, times(1)).removeUserFromProject(user.getId(), project.getId());
    }
}
