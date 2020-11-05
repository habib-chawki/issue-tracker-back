package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
        assertThat(savedIssue).isNotNull();
    }

    @Test
    public void itShouldFindIssueById(){
        // save a new issue
        Issue issue = issueRepository.save(new Issue());

        // find the issue by id
        Optional<Issue> response = issueRepository.findById(issue.getId());

        // expect the issue to have been found successfully
        assertThat(response.get().getId()).isEqualTo(issue.getId());
    }

    @Test
    public void itShouldFindAllIssues(){
        // save a list of issues
        Issue issue1 = issueRepository.save(new Issue());
        Issue issue2 = issueRepository.save(new Issue());
        Issue issue3 = issueRepository.save(new Issue());

        // find all issues
        Iterable<Issue> issues = issueRepository.findAll();

        // assert that all issues have been retrieved successfully
        assertThat(issues).contains(issue1);
        assertThat(issues).contains(issue2);
        assertThat(issues).contains(issue3);
    }
}
