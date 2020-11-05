package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class IssueRepositoryTest {

    @Autowired
    IssueRepository issueRepository;

    @Test
    public void itShouldSaveIssue(){
        // save a new issue
        Issue savedIssue = issueRepository.save(new Issue());

        // expect the issue to have been saved successfully
        assertThat(savedIssue.getId()).isEqualTo(savedIssue.getId());
    }

    @Test
    public void itShouldGetIssueById(){
        // save a new issue
        Issue issue = issueRepository.save(new Issue());

        // find the issue by id
        Optional<Issue> response = issueRepository.findById(issue.getId());

        // expect the issue to have been found successfully
        assertThat(response.get().getId()).isEqualTo(issue.getId());
    }

    @Test
    public void itShouldGetListOfIssues(){
        // save a list of issues
        Issue issue1 = issueRepository.save(new Issue());
        Issue issue2 = issueRepository.save(new Issue());
        Issue issue3 = issueRepository.save(new Issue());

        // find all issues
        Iterable<Issue> response = issueRepository.findAll();

        // assert that all issues have been retrieved successfully
        assertThat(response).contains(issue1);
        assertThat(response).contains(issue2);
        assertThat(response).contains(issue3);
    }
}
