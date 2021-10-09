package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.issue.IssueService;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectService;
import com.habibInc.issueTracker.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SprintServiceTest {
    @Spy
    @InjectMocks
    SprintService sprintService;

    @Mock
    SprintRepository sprintRepository;

    @Mock
    IssueRepository issueRepository;

    @Mock
    ProjectService projectService;

    @Mock
    IssueService issueService;

    Sprint sprint;
    List<Issue> issues;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        sprint = Sprint.builder()
                .id(1L)
                .name("First sprint")
                .goal("sprint goal")
                .status(SprintStatus.INACTIVE)
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
    public void itShouldCreateSprint() {
        // given the project id
        Long projectId = 100L;

        // given the repository response
        when(sprintRepository.save(sprint)).thenReturn(sprint);
        when(projectService.getProjectById(projectId)).thenReturn(new Project());

        // when sprintService#createSprint() is invoked
        Sprint createdSprint = sprintService.createSprint(projectId, sprint);

        // expect the repository to have been invoked
        verify(sprintRepository, times(1)).save(sprint);

        // then expect the sprint to have been created successfully
        assertThat(createdSprint).isEqualTo(sprint);
    }

    @Test
    public void givenCreateSprint_itShouldSetTheProject() {
        // given a project
        Project project = new Project();
        project.setId(666L);
        project.setName("Project");

        when(projectService.getProjectById(project.getId())).thenReturn(project);
        when(sprintRepository.save(sprint)).thenReturn(sprint);

        // when the sprint is created
        Sprint sprint = sprintService.createSprint(project.getId(), this.sprint);

        // then the its project should be set
        assertThat(sprint.getProject()).isEqualTo(project);
    }

    @Test
    public void itShouldGetSprintById() {
        // given the repository response
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));

        // when the sprint service is invoked to fetch a sprint by id
        Sprint retrievedSprint = sprintService.getSprintById(sprint.getId());

        // then the repository should be invoked and the sprint should be fetched successfully
        verify(sprintRepository, times(1)).findById(sprint.getId());
        assertThat(retrievedSprint).isEqualTo(sprint);
    }

    @Test
    public void givenGetSprintById_whenSprintDoesNotExist_itShouldReturnSprintNotFoundError() {
        // when the sprint does not exist
        when(sprintRepository.findById(404L)).thenReturn(Optional.ofNullable(null));

        // then a sprint not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class).isThrownBy(
                () -> sprintService.getSprintById(404L)
        ).withMessageContaining("Sprint not found");
    }

    @Test
    public void itShouldSetSprintBacklog() {
        // expect the issue repository to have been invoked
        sprintService.setSprintBacklog(sprint.getId(), new ArrayList<>());
        verify(issueRepository, times(1)).updateIssuesSprint(eq(sprint.getId()), any(List.class));
    }

    @Test
    public void itShouldGetSprintsByStatus() {
        // given the sprint repository
        when(sprintRepository.findAllByStatus(SprintStatus.ACTIVE)).thenReturn(new ArrayList<>());

        // when the service method is invoked
        sprintService.getSprintsByStatus(SprintStatus.ACTIVE);

        // then expect the repository to have been invoked
        verify(sprintRepository, times(1)).findAllByStatus(SprintStatus.ACTIVE);
    }

    @Test
    public void itShouldUpdateSprintStatus() {
        // given the sprint repository
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(sprint)).thenReturn(sprint);

        // when the service method is invoked
        sprintService.updateSprintStatus(sprint.getId(), SprintStatus.ACTIVE);

        // then expect the status to have been updated
        assertThat(sprint.getStatus()).isEqualTo(SprintStatus.ACTIVE);

        verify(sprintRepository, times(1)).findById(sprint.getId());
        verify(sprintRepository, times(1)).save(sprint);
    }

    @Test
    public void givenUpdateSprintStatus_whenStatusIsOver_itShouldSetSprintToNull() {
        // given the sprint repository
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(sprint)).thenReturn(sprint);

        doNothing().when(sprintService).moveUnfinishedIssuesToProductBacklog(sprint);

        // when the service method is invoked
        sprintService.updateSprintStatus(sprint.getId(), SprintStatus.OVER);

        // then expect the status to have been updated
        assertThat(sprint.getStatus()).isEqualTo(SprintStatus.OVER);

        verify(sprintRepository, times(1)).findById(sprint.getId());
        verify(sprintRepository, times(1)).save(sprint);
    }

    @Test
    public void itShouldUpdateIssueSprint() {
        // given an issue
        Issue issue = Issue.builder().id(100L).summary("issue").build();

        // given
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(issueService.getIssueById(issue.getId())).thenReturn(issue);

        when(issueRepository.save(issue)).thenReturn(issue);

        // when the service method is invoked
        sprintService.updateIssueSprint(null, issue.getId(), sprint.getId());

        // then the sprint should have been updated
        assertThat(issue.getSprint()).isEqualTo(sprint);
    }

    @Test
    public void itShouldDeleteSprintById() {
        // given the project and its owner
        final User projectOwner = User.builder().id(200L).build();
        final Project project = Project.builder().id(100L).owner(projectOwner).build();

        // given the sprint project is set
        sprint.setProject(project);

        // given the sprint backlog
        sprint.setBacklog(new ArrayList<>());

        // given the sprint and project are found by id
        doReturn(sprint).when(sprintService).getSprintById(sprint.getId());
        doReturn(project).when(projectService).getProjectById(project.getId());

        // when the service method to delete a sprint by id is invoked
        sprintService.deleteSprintById(project.getId(), sprint.getId(), projectOwner);

        // then expect the repository to have been invoked and the sprint to have been deleted
        verify(sprintRepository, times(1)).deleteById(sprint.getId());
    }

    @Test
    public void itShouldNotDeleteSprintWhenTheAuthenticatedUserIsNotTheProjectOwner() {
        // given the project
        final Project project = Project.builder().id(777L).owner(User.builder().id(999L).build()).build();

        // given a random user
        final User notProjectOwner = User.builder().id(666L).build();

        // given the sprint project is set
        sprint.setProject(project);

        // given the sprint and project are found by id
        doReturn(sprint).when(sprintService).getSprintById(sprint.getId());
        doReturn(project).when(projectService).getProjectById(project.getId());

        // when the service is invoked to delete the sprint by someone other than the project owner
        // then expect a forbidden operation exception to have been thrown
        assertThatExceptionOfType(ForbiddenOperationException.class).isThrownBy(
                () -> sprintService.deleteSprintById(project.getId(), sprint.getId(), notProjectOwner)
        );
    }
}
