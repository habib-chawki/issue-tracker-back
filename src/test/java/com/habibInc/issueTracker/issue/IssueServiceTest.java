package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
    public void itShouldGetIssueById(){
        // mock repository behaviour and return an issue optional
        Issue issue = new Issue(1L);
        when(issueRepository.findById(anyLong())).thenReturn(Optional.of(issue));

        Issue returnedIssue = issueService.getIssue(1L);

        assertThat(returnedIssue.getId()).isEqualTo(1L);
    }

    @Test
    public void itShouldCreateIssue(){
        // return created issue
        Issue issue = new Issue(1L);
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);

        Issue returnedIssue = issueService.createIssue(issue);

        assertThat(returnedIssue.getId()).isEqualTo(1L);
    }
}
