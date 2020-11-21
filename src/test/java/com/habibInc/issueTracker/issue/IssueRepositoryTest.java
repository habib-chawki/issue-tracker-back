package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.comment.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class IssueRepositoryTest {

    @Autowired
    IssueRepository issueRepository;

    Issue issue1, issue2;

    Comment comment1, comment2;
    List<Comment> comments;

    @BeforeEach
    public void init() {
        // create issue
        issue1 = new Issue();
        issue2 = new Issue();

        // create list of comments
        comment1 = new Comment("owner 1", "comment 1");
        comment2 = new Comment("owner 2", "comment 2");
        comments = Arrays.asList(comment1, comment2);

        // set up issue1 properties
        issue1.setId(1L);
        issue1.setIssueKey("KJ54d3");
        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");
        issue1.setType(IssueType.STORY);
        issue1.setResolution(IssueResolution.DONE);
        issue1.setComments(comments);
        issue1.setVotes(5);
        issue1.setAssignee("Me");
        issue1.setReporter("Jon Doe");
        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setEstimate(LocalTime.of(2, 0));

        // set up issue2 properties
        issue2.setId(2L);
        issue2.setIssueKey("YF8E33");
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setResolution(IssueResolution.DUPLICATE);
        issue1.setComments(comments);
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
        Issue issue = issueRepository.save(issue2);

        // find the issue by id
        Optional<Issue> response = issueRepository.findById(issue.getId());

        // expect the issue to have been found successfully
        assertThat(response.get()).isEqualTo(issue2);
    }

    @Test
    public void itShouldFindAllIssues(){
        // given a list of issues
        issueRepository.save(issue1);
        issueRepository.save(issue2);

        // find all issues
        Iterable<Issue> issues = issueRepository.findAll();

        // assert that all issues have been retrieved successfully
        assertThat(issues).contains(issue1);
        assertThat(issues).contains(issue2);
    }
}
