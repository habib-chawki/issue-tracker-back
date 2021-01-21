package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.column.ColumnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class IssueRepositoryTest {

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    ColumnRepository columnRepository;

    Issue issue1, issue2;

    @BeforeEach
    public void init() {
        // create issue
        issue1 = new Issue();
        issue2 = new Issue();

        // set up issue1 properties
        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");
        issue1.setType(IssueType.STORY);
        issue1.setResolution(IssueResolution.DONE);
        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setEstimate(LocalTime.of(2, 0));

        // set up issue2 properties
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setResolution(IssueResolution.DUPLICATE);
        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setEstimate(LocalTime.of(6, 15));
    }

    @Test
    public void itShouldSaveIssue(){
        // save a new issue
        Issue savedIssue = issueRepository.save(issue1);

        // expect the issue to have been saved successfully
        assertThat(savedIssue).isEqualToComparingOnlyGivenFields(issue1);

        // expect the id to have been generated
        assertThat(savedIssue.getId()).isPositive().isNotNull();
    }

    @Test
    public void itShouldFindIssueById(){
        // save a new issue
        Issue savedIssue = issueRepository.save(issue2);

        // find the issue by id
        Optional<Issue> response = issueRepository.findById(savedIssue.getId());

        // expect the issue to have been found successfully
        assertThat(response.get()).isEqualTo(savedIssue);
    }

    @Test
    public void itShouldFindAllIssues(){
        // given a list of issues
        Issue savedIssue1 = issueRepository.save(issue1);
        Issue savedIssue2 = issueRepository.save(issue2);

        // find all issues
        Iterable<Issue> issues = issueRepository.findAll();

        // assert that all issues have been retrieved successfully
        assertThat(issues).contains(savedIssue1);
        assertThat(issues).contains(savedIssue2);
    }

    @Test
    public void itShouldDeleteIssueById() {
        // given an issue is saved
        Issue savedIssue = issueRepository.save(issue2);

        // the issue should be present before deleting
        Optional<Issue> before = issueRepository.findById(savedIssue.getId());
        assertThat(before.isPresent()).isTrue();

        // when the issue is deleted
        issueRepository.deleteById(savedIssue.getId());

        // then it should not be present afterwards
        Optional<Issue> after = issueRepository.findById(savedIssue.getId());
        assertThat(after.isPresent()).isFalse();
    }

    @Test
    public void itShouldFindPaginatedListOfIssuesByColumnId () {
        // given a column
        Column column = new Column();
        column.setTitle("Issues column");

        column = columnRepository.save(column);

        // given a list of issues
        Issue issue1 = Issue.builder().column(column).summary("issue 1").build();
        Issue issue2 = Issue.builder().column(column).summary("issue 2").build();
        Issue issue3 = Issue.builder().column(column).summary("issue 3").build();
        Issue issue4 = Issue.builder().column(column).summary("issue 4").build();
        Issue issue5 = Issue.builder().column(column).summary("issue 5").build();

        List<Issue> issues = List.of(issue1, issue2, issue3, issue4, issue5);
        issueRepository.saveAll(issues);

        // given a Pageable
        int page = 0;
        int size = 3;
        Pageable pageable = PageRequest.of(page, size);

        // when find by column id is invoked
        List<Issue> paginatedIssuesList =
                issueRepository.findByColumnId(column.getId(), pageable);

        // then expect the paginated list of issues to be returned
        assertThat(paginatedIssuesList.size()).isEqualTo(size);
        assertThat(paginatedIssuesList).isEqualTo(issues.subList(0, size));
    }
}
