package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SprintServiceTest {
    @InjectMocks
    SprintService sprintService;

    @Mock
    SprintRepository sprintRepository;

    @Mock
    IssueRepository issueRepository;

    @Mock
    ProjectService projectService;

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
    public void itShouldSetSprintIssues() {
        // given the sprint
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(issueRepository.saveAll(issues)).thenReturn(issues);

        // when the service is invoked to add issues to the sprint
        sprintService.setSprintIssues(sprint.getId(), issues);

        // then each issue's sprint property should be updated
        issues.forEach((Issue issue) -> assertThat(issue.getSprint()).isEqualTo(sprint));
    }
}
