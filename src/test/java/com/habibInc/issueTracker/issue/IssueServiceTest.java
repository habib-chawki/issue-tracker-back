package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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

    @BeforeEach
    public void setup(){
        // initialize mocks
        initMocks(this);
    }

    @Test
    public void itShouldCreateIssue(){
        // mock repository and create a new issue
        Issue issue = new Issue(1L);
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        // create the issue
        Issue createdIssue = issueService.createIssue(issue);

        // expect the issue to have been created successfully
        assertThat(createdIssue.getId()).isEqualTo(1L);
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
