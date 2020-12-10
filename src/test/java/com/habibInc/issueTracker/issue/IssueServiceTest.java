package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

public class IssueServiceTest {

    // inject mocks into issue service
    @InjectMocks
    IssueService issueService;

    // create repository mock
    @Mock
    IssueRepository issueRepository;

    Issue issue1, issue2;

    @BeforeEach
    public void init() {
        // initialize mocks
        initMocks(this);
    }

    @BeforeEach
    public void setup() {
        // create issue
        issue1 = new Issue();
        issue2 = new Issue();

        // set up issue1 properties
        issue1.setId(1L);
        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");
        issue1.setType(IssueType.STORY);
        issue1.setResolution(IssueResolution.DONE);
        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setEstimate(LocalTime.of(2, 0));

        // set up issue2 properties
        issue2.setId(2L);
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setResolution(IssueResolution.DUPLICATE);
        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setEstimate(LocalTime.of(6, 15));
    }

    @Test
    public void itShouldCreateIssue() {
        // mock repository and create a new issue
        when(issueRepository.save(issue1)).thenReturn(issue1);

        // create the issue
        Issue createdIssue = issueService.createIssue(issue1, null);

        // expect the issue to have been created successfully
        assertThat(createdIssue).isEqualTo(issue1);
    }

    @Test
    public void itShouldGetIssueById() {
        // mock repository behaviour and return an issue optional
        when(issueRepository.findById(2L)).thenReturn(Optional.of(issue2));

        // get the issue by id
        Issue returnedIssue = issueService.getIssue(2L);

        // expect the proper issue to have been retrieved
        assertThat(returnedIssue).isEqualTo(issue2);
    }

    @Test
    public void givenGetIssueById_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
        // when the issue does not exist
        String errorMessage = "Issue not found";
        when(issueRepository.findById(10L))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        // then an issue not found exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> issueService.getIssue(10L));
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
        Issue returnedIssue = issueService.updateIssue(1L, updatedIssue);

        // then expect the response to be the updated issue
        assertThat(returnedIssue).isEqualTo(updatedIssue);
    }

    @Test
    public void givenUpdateIssue_whenIssueDoesNotExist_itShouldReturnIssueNotFoundError() {
        String errorMessage = "Issue not found";

        // when trying to update an issue that does not exist
        when(issueRepository.findById(10L)).thenThrow(new ResourceNotFoundException(errorMessage));

        // then a 404 issue not found error should be returned
        assertThrows(ResourceNotFoundException.class,
                () -> issueService.updateIssue(10L, issue1));
    }
}
