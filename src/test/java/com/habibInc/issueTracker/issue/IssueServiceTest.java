package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectService;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

public class IssueServiceTest {

    // inject mocks into issue service
    @InjectMocks
    IssueService issueService;

    // create repository mock
    @Mock
    IssueRepository issueRepository;

    @Mock
    ProjectService projectService;

    @Mock
    UserService userService;

    Issue issue1, issue2;
    User authenticatedUser;

    @BeforeEach
    public void init() {
        // initialize mocks
        initMocks(this);
    }

    @BeforeEach
    public void setup() {
        // set an authenticated user
        authenticatedUser = new User();
        authenticatedUser.setId(100L);
        authenticatedUser.setEmail("authenticated.user@email.com");

        // create a reporter for issue2
        User reporter = new User();
        reporter.setId(200L);
        reporter.setEmail("issue2.reporter@email.com");

        // create issue
        issue1 = new Issue();
        issue2 = new Issue();

        // set up issue1 properties
        issue1.setId(1L);
        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");
        issue1.setType(IssueType.STORY);
        issue1.setStatus(IssueStatus.RESOLVED);
        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setPoints(4);
        issue1.setPosition(1);

        // set the authenticated user as the reporter
        issue1.setReporter(authenticatedUser);

        // set up issue2 properties
        issue2.setId(2L);
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setStatus(IssueStatus.IN_PROGRESS);
        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setPoints(8);
        issue2.setPosition(2);

        // set another reporter for issue2
        issue2.setReporter(reporter);
    }

    @Test
    public void itShouldCreateIssue() {
        // mock repository and create a new issue
        when(issueRepository.save(issue1)).thenReturn(issue1);

        // create the issue
        Issue createdIssue = issueService.createIssue(issue1, null, null);

        // expect the repository to have been invoked and the issue to have been created
        verify(issueRepository, times(1)).save(issue1);
        assertThat(createdIssue).isEqualTo(issue1);
    }

    @Test
    public void givenCreateIssue_itShouldSetReporter() {
        // given a reporter
        User reporter = new User();
        reporter.setEmail("reporter@issue.me");
        reporter.setPassword("reporter");

        when(issueRepository.save(issue2)).thenReturn(issue2);

        // when an issue is created
        Issue createdIssue = issueService.createIssue(issue2, reporter, null);

        // then expect the reporter to have been set
        assertThat(createdIssue.getReporter()).isEqualTo(reporter);
    }

    @Test
    public void givenCreateIssue_itShouldSetProject() {
        // given the project
        Project project = new Project();
        project.setId(100L);
        project.setName("Proj");

        when(projectService.getProjectById(project.getId())).thenReturn(project);
        when(issueRepository.save(issue1)).thenReturn(issue1);

        // when an issue is created
        Issue createdIssue = issueService.createIssue(issue1, null, project.getId());

        // then its project property should be set
        assertThat(createdIssue.getProject()).isEqualTo(project);
    }

    @Test
    public void givenCreateIssue_itShouldSetPosition() {
        // given the project id
        Long projectId = 100L;

        // given the issues count
        int issuesCount = 5;

        when(issueRepository.save(issue1)).thenReturn(issue1);
        when(issueRepository.countByProjectId(projectId)).thenReturn(issuesCount);

        // when the service is invoked to create the issue
        issueService.createIssue(issue1, authenticatedUser, projectId);

        // then expect the issue's position to have been set
        assertThat(issue1.getPosition()).isEqualTo(issuesCount+1);
        verify(issueRepository, times(1)).countByProjectId(projectId);
    }

    @Test
    public void itShouldGetIssueById() {
        // mock repository behaviour and return an issue optional
        when(issueRepository.findById(2L)).thenReturn(Optional.of(issue2));

        // get the issue by id
        Issue returnedIssue = issueService.getIssueById(2L);

        // expect the proper issue to have been retrieved
        assertThat(returnedIssue).isEqualTo(issue2);
    }

    @Test
    public void givenGetIssueById_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
        // when the issue does not exist
        String errorMessage = "Issue not found";

        when(issueRepository.findById(10L)).thenReturn(Optional.ofNullable(null));

        // then an issue not found exception is thrown
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> issueService.getIssueById(10L))
                .withMessage(errorMessage);
    }

    @Test
    public void itShouldGetAllIssues() {
        // given a list of issues
        List<Issue> issues = Arrays.asList(issue1, issue2);

        // when issueRepository is invoked return the list of issues
        when(issueRepository.findAll()).thenReturn(issues);

        // retrieve list of issues
        Iterable<Issue> returnedIssues = issueService.getAllIssues();

        // expect all issues to have been retrieved successfully
        assertThat(returnedIssues).contains(issue1);
        assertThat(returnedIssues).contains(issue2);
    }

    @Test
    public void itShouldUpdateIssue() throws Exception {
        // register JavaTimeModule to fix "can not construct instance of java.time.LocalDate" error
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // copy the issue
        String issueJson = mapper.writeValueAsString(issue1);
        Issue updatedIssue = mapper.readValue(issueJson, Issue.class);

        // update the issue
        updatedIssue.setSummary("updated summary");
        updatedIssue.setType(IssueType.BUG);

        // mock the issue repository behaviour
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue1));
        when(issueRepository.save(updatedIssue)).thenReturn(updatedIssue);

        // when the updateIssue service method is invoked
        Issue returnedIssue = issueService.updateIssue(1L, updatedIssue, authenticatedUser);

        // then expect the response to be the updated issue
        assertThat(returnedIssue).isEqualTo(updatedIssue);
    }

    @Test
    public void givenUpdateIssue_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
        String errorMessage = "Issue not found";

        // when trying to update an issue that does not exist
        when(issueRepository.findById(10L)).thenReturn(Optional.ofNullable(null));

        // then a 404 issue not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> issueService.updateIssue(10L, issue1, authenticatedUser))
                .withMessage(errorMessage);
    }

    @Test
    public void givenUpdateIssue_whenAuthenticatedUserIsNotTheReporter_itShouldReturnForbiddenOperationError() {
        when(issueRepository.findById(2L)).thenReturn(Optional.of(issue2));

        // when authenticated user is not the reporter then they should not be authorized to update
        assertThatExceptionOfType(ForbiddenOperationException.class)
                .isThrownBy(() -> issueService.updateIssue(2L, issue2, authenticatedUser))
                .withMessageContaining("Forbidden");
    }

    @Test
    public void itShouldDeleteIssue() {
        // given the issue exists
        when(issueRepository.findById(issue1.getId())).thenReturn(Optional.of(issue1));

        // when deleteIssue() is called
        issueService.deleteIssue(issue1.getId(), authenticatedUser);

        // then expect the issue repository to have been invoked
        verify(issueRepository).deleteById(issue1.getId());
    }

    @Test
    public void givenDeleteIssue_whenAuthenticatedUserIsNotTheReporter_itShouldReturnForbiddenOperationError() {
        when(issueRepository.findById(2L)).thenReturn(Optional.of(issue2));

        assertThatExceptionOfType(ForbiddenOperationException.class)
                .isThrownBy(() -> issueService.deleteIssue(2L, authenticatedUser))
                .withMessageContaining("Forbidden");
    }

    @Test
    public void itShouldUpdateIssueAssignee() {
        // given a user
        User assignee = new User();
        assignee.setId(100L);
        assignee.setEmail("assignee@user");
        assignee.setPassword("assignee_pass");

        when(userService.getUserById(assignee.getId())).thenReturn(assignee);
        when(issueRepository.findById(issue1.getId())).thenReturn(Optional.of(issue1));

        // when the update assignee request is made
        issueService.updateIssueAssignee(issue1.getId(), assignee.getId());

        // then expect the issue assignee to have been updated successfully
        assertThat(issue1.getAssignee()).isEqualTo(assignee);

        verify(issueRepository, times(1)).findById(issue1.getId());
    }

    @Test
    public void itShouldSwapThePositionsOfTwoIssues() {
        // given the project
        Project project = Project.builder().id(100L).name("project swap").build();

        // given the issues belong to the same project
        issue1.setProject(project);
        issue2.setProject(project);

        // given the issues positions before the swap
        final int position1 = issue1.getPosition();
        final int position2 = issue2.getPosition();

        // given
        when(projectService.getProjectById(project.getId())).thenReturn(project);
        when(issueRepository.findById(issue1.getId())).thenReturn(Optional.of(issue1));
        when(issueRepository.findById(issue2.getId())).thenReturn(Optional.of(issue2));

        // when the service is invoked to swap issues' positions
        issueService.swapIssuesPositions(project.getId(), issue1.getId(), issue2.getId());

        // then expect the positions to have been swapped
        assertThat(issue1.getPosition()).isEqualTo(position2);
        assertThat(issue2.getPosition()).isEqualTo(position1);

        verify(projectService, times(1)).getProjectById(project.getId());
    }

    @Test
    public void itShouldOnlySwapIssues_whenTheyBelongToTheSameProject() {
        // given the projects
        final Project project = Project.builder().id(100L).name("main project").build();
        final Project otherProject = Project.builder().id(666L).name("other project").build();

        // given the issues belong to different projects
        issue1.setProject(project);
        issue2.setProject(otherProject);

        // given the project service response
        when(projectService.getProjectById(project.getId())).thenReturn(project);
        when(issueRepository.findById(issue1.getId())).thenReturn(Optional.of(issue1));
        when(issueRepository.findById(issue2.getId())).thenReturn(Optional.of(issue2));

        // when the service is invoked to swap the issues' positions
        // then the swap operation should be forbidden
        assertThatExceptionOfType(ForbiddenOperationException.class).
                isThrownBy(() -> issueService.swapIssuesPositions(project.getId(), issue2.getId(), issue1.getId()));
    }
}
