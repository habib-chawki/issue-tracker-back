package com.habibInc.issueTracker.issue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class IssueRepositoryTest {

    @Autowired
    IssueRepository issueRepository;

    Issue issue1, issue2;

    @BeforeEach
    public void setup(){
        // create issue
        issue1 = new Issue();

        issue1.setIssueKey("KJ54d3");

        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");

        issue1.setType(IssueType.STORY);
        issue1.setResolution(IssueResolution.DONE);

        issue1.setComments("comments");
        issue1.setVotes(5);

        issue1.setAssignee("Me");
        issue1.setReporter("Jon Doe");

        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setEstimate(LocalTime.of(2, 0));

        // create another issue
        issue2 = new Issue();

        issue2.setIssueKey("YF8E33");

        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");

        issue2.setType(IssueType.TASK);
        issue2.setResolution(IssueResolution.DUPLICATE);

        issue2.setComments("comments");
        issue2.setVotes(3);

        issue2.setAssignee("You");
        issue2.setReporter("Jane Doe");

        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setEstimate(LocalTime.of(6, 15));
    }

    @Test
    public void itShouldSaveIssue(){
        // save a new issue
        Issue savedIssue = issueRepository.save(issue1);

        // expect the issue to have been saved successfully
        assertThat(savedIssue).isEqualTo(issue1);

        // expect the id to have been generated
        assertThat(savedIssue.getId()).isNotNull();
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
