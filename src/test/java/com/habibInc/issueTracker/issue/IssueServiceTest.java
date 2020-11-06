package com.habibInc.issueTracker.issue;

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
    public void setup(){
        // initialize mocks
        initMocks(this);

        // create issue
        issue1 = new Issue();

        issue1.setId(1L);
        issue1.setKey("KJ54d3");

        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");

        issue1.setType(IssueType.STORY);
        issue1.setResolution(IssueResolution.DONE);

        issue1.setComments(Arrays.asList("comment 1", "comment 2"));
        issue1.setVotes(5);

        issue1.setAssignee("Me");
        issue1.setReporter("Jon Doe");

        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setEstimate(LocalTime.of(2, 0));

        // create another issue
        issue2 = new Issue();

        issue2.setId(2L);
        issue2.setKey("YF8E33");

        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");

        issue2.setType(IssueType.TASK);
        issue2.setResolution(IssueResolution.DUPLICATE);

        issue2.setComments(Arrays.asList("comment 1", "comment 2", "comment 3"));
        issue2.setVotes(3);

        issue2.setAssignee("You");
        issue2.setReporter("Jane Doe");

        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setEstimate(LocalTime.of(6, 15));
    }

    @Test
    public void itShouldCreateIssue(){
        // mock repository and create a new issue
        when(issueRepository.save(any(Issue.class))).thenReturn(issue1);

        // create the issue
        Issue createdIssue = issueService.createIssue(issue1);

        // expect the issue to have been created successfully
        assertThat(createdIssue).isEqualTo(issue1);
    }

    @Test
    public void itShouldGetIssueById(){
        // mock repository behaviour and return an issue optional
        Issue issue = new Issue(1L);
        when(issueRepository.findById(anyLong())).thenReturn(Optional.of(issue));

        // get the issue by id
        Issue returnedIssue = issueService.getIssue(1L);

        // expect the proper issue to have been retrieved
        assertThat(returnedIssue.getId()).isEqualTo(1L);
    }

    @Test
    public void itShouldGetAllIssues(){
        // create a list of mocked issues
        Issue issue1 = new Issue();
        Issue issue2 = new Issue();
        List<Issue> mockedIssues = Arrays.asList(issue1, issue2);

        when(issueRepository.findAll()).thenReturn(mockedIssues);

        // retrieve list of issues
        Iterable<Issue> issues = issueService.getAllIssues();

        // expect all issues to have been retrieved successfully
        assertThat(issues).contains(issue1);
        assertThat(issues).contains(issue2);
    }
}
