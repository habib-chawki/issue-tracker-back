package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.column.ColumnRepository;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectRepository;
import com.habibInc.issueTracker.sprint.Sprint;
import com.habibInc.issueTracker.sprint.SprintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IssueRepositoryTest {

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    ColumnRepository columnRepository;

    @Autowired
    SprintRepository sprintRepository;

    @Autowired
    ProjectRepository projectRepository;

    Issue issue1, issue2;
    Project project;
    Sprint sprint;

    @BeforeEach
    public void init() {
        // create issue
        issue1 = new Issue();
        issue2 = new Issue();

        // set up issue1 properties
        issue1.setSummary("Issue 1 summary");
        issue1.setDescription("Issue 1 description");
        issue1.setType(IssueType.STORY);
        issue1.setStatus(IssueStatus.RESOLVED);
        issue1.setCreationTime(LocalDateTime.now());
        issue1.setUpdateTime(LocalDateTime.now());
        issue1.setPosition(11);
        issue1.setPoints(4);

        // set up issue2 properties
        issue2.setSummary("Issue 2 summary");
        issue2.setDescription("Issue 2 description");
        issue2.setType(IssueType.TASK);
        issue2.setStatus(IssueStatus.IN_PROGRESS);
        issue2.setCreationTime(LocalDateTime.now());
        issue2.setUpdateTime(LocalDateTime.now());
        issue2.setPosition(22);
        issue2.setPoints(21);

        // create a project
        project = new Project();
        project.setName("Project");

        // create a sprint
        sprint = Sprint.builder().name("Sprint").build();
    }

    @Test
    public void itShouldSaveIssue() {
        // save a new issue
        Issue savedIssue = issueRepository.save(issue1);

        // expect the issue to have been saved successfully
        assertThat(savedIssue).isEqualToComparingOnlyGivenFields(issue1);

        // expect the id to have been generated
        assertThat(savedIssue.getId()).isPositive().isNotNull();
    }

    @Test
    public void itShouldFindIssueById() {
        // save a new issue
        Issue savedIssue = issueRepository.save(issue2);

        // find the issue by id
        Optional<Issue> response = issueRepository.findById(savedIssue.getId());

        // expect the issue to have been found successfully
        assertThat(response.get()).isEqualTo(savedIssue);
    }

    @Test
    public void itShouldFindAllIssues() {
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
    public void itShouldFindPaginatedListOfIssuesByColumnId() {
        // given a column
        Column column = new Column();
        column.setTitle("Issues column");

        column = columnRepository.save(column);

        // given a list of issues
        List<Issue> issues = List.of(
                Issue.builder().column(column).summary("issue 1").build(),
                Issue.builder().column(column).summary("issue 2").build(),
                Issue.builder().column(column).summary("issue 3").build(),
                Issue.builder().column(column).summary("issue 4").build(),
                Issue.builder().column(column).summary("issue 5").build()
        );

        issueRepository.saveAll(issues);

        // given a Pageable
        int page = 0;
        int size = 3;
        Pageable pageable = PageRequest.of(page, size);

        // when findByColumnId() is invoked
        List<Issue> paginatedIssuesList =
                issueRepository.findByColumnId(column.getId(), pageable);

        // then expect the paginated list of issues to be returned
        assertThat(paginatedIssuesList.size()).isEqualTo(size);
        assertThat(paginatedIssuesList).isEqualTo(issues.subList(0, size));
    }

    @Test
    public void itShouldUpdateIssueColumn() {
        // given two distinct columns
        Column column = new Column();
        column.setTitle("Column");
        column = columnRepository.save(column);

        Column column2 = new Column();
        column2.setTitle("Column 02");
        column2 = columnRepository.save(column2);

        // given an issue belonging to the first column
        Issue issue = Issue.builder().column(column).summary("issue 1").build();
        issue = issueRepository.save(issue);

        // when a request to update the issue column is made (imitates a transfer)
        issueRepository.updateIssueColumn(issue.getId(), column2.getId());

        // then expect the update to be successful (issue transferred to another column)
        assertThat(issueRepository.findById(issue.getId()).get().getColumn()).isEqualTo(column2);
    }

    @Test
    public void itShouldUpdateIssuesSprint() {
        // given a list of issues
        List<Issue> issues = List.of(
                Issue.builder().summary("issue 1").build(),
                Issue.builder().summary("issue 2").build(),
                Issue.builder().summary("issue 3").build(),
                Issue.builder().summary("issue 4").build(),
                Issue.builder().summary("issue 5").build()
        );
        issues = (List<Issue>) issueRepository.saveAll(issues);

        // given a sprint
        Sprint sprint = new Sprint();
        sprint.setName("Primary sprint");
        sprint = sprintRepository.save(sprint);

        // given a list of issues ids
        List<Long> issuesIds =
                issues.stream().map((issue) -> issue.getId()).collect(Collectors.toList());

        // when a request is made to set the sprint backlog
        int numOfUpdatedIssues = issueRepository.updateIssuesSprint(sprint.getId(), issuesIds);

        // then expect all issues to have been updated
        assertThat(numOfUpdatedIssues).isEqualTo(issues.size());

        // expect the sprint of each issue to have been set
        List<Issue> updatedIssues = (List<Issue>) issueRepository.findAll();
        for (Issue issue : updatedIssues)
            assertThat(issue.getSprint()).isEqualTo(sprint);
    }

    @Test
    public void itShouldUpdateIssuesColumn() {
        // given a list of issues
        List<Issue> issues = List.of(
                Issue.builder().summary("issue 1").build(),
                Issue.builder().summary("issue 2").build(),
                Issue.builder().summary("issue 3").build(),
                Issue.builder().summary("issue 4").build(),
                Issue.builder().summary("issue 5").build()
        );

        issues = (List<Issue>) issueRepository.saveAll(issues);

        // given a column
        Column column = new Column();
        column.setTitle("Column");
        column = columnRepository.save(column);

        // given the list of issue ids
        List<Long> issueIds = issues.stream().map((issue) -> issue.getId()).collect(Collectors.toList());

        // when a request is made to update the column of the list of issues
        issueRepository.updateIssuesColumn(column.getId(), issueIds);

        // then expect the column of each issue to be the to do column
        List<Issue> updatedIssues = (List<Issue>) issueRepository.findAll();
        for (Issue issue : updatedIssues)
            assertThat(issue.getColumn()).isEqualTo(column);
    }

    @Test
    public void itShouldFindAllIssuesByProjectId() {
        // given a project
        project = projectRepository.save(project);

        // given the project backlog
        List<Issue> backlog = List.of(
                Issue.builder().project(project).summary("issue 1").build(),
                Issue.builder().project(project).summary("issue 2").build(),
                Issue.builder().project(project).summary("issue 2").build()
        );

        // given the backlog is saved
        backlog = (List<Issue>) issueRepository.saveAll(backlog);

        // when issueRepository#findAllByProjectId is invoked
        List<Issue> retrievedBacklog =
                issueRepository.findAllByProjectId(project.getId());

        // then expect the project backlog to have been retrieved successfully
        assertThat(retrievedBacklog).hasSameElementsAs(backlog);
    }


    @Test
    public void givenFindIssues_whenSprintIsNotNull_itShouldReturnSprintBacklog() {
        // given a project
        project = projectRepository.save(project);

        // given a sprint
        sprint.setProject(project);
        sprint = sprintRepository.save(sprint);

        // given a list of issues belonging to the sprint
        List<Issue> issuesWithSprint = List.of(
                Issue.builder().project(project).sprint(sprint).summary("issue 1").build(),
                Issue.builder().project(project).sprint(sprint).summary("issue 2").build(),
                Issue.builder().project(project).sprint(sprint).summary("issue 3").build()
        );

        issuesWithSprint = (List<Issue>) issueRepository.saveAll(issuesWithSprint);

        // when "findAllByProjectIdAndSprintId()" is invoked with a sprint id
        List<Issue> issues =
                issueRepository.findAllByProjectIdAndSprintId(project.getId(), sprint.getId());

        // then all the issues belonging to the sprint should be retrieved
        assertThat(issues).containsExactlyElementsOf(issuesWithSprint);
    }

    @Test
    public void givenFindIssues_whenSprintIsNull_itShouldReturnProductBacklog() {
        // given a project
        project = projectRepository.save(project);

        // given a list of issues without a sprint
        List<Issue> issuesWithoutSprint = List.of(
                Issue.builder().project(project).summary("issue 10").build(),
                Issue.builder().project(project).summary("issue 20").build(),
                Issue.builder().project(project).summary("issue 30").build()
        );

        issuesWithoutSprint = (List<Issue>) issueRepository.saveAll(issuesWithoutSprint);

        // when "findAllByProjectIdAndSprintId()" is invoked with a null sprint id
        List<Issue> issues = issueRepository.findAllByProjectIdAndSprintId(project.getId(), null);

        // then all the issues without a sprint should be retrieved
        assertThat(issues).containsExactlyElementsOf(issuesWithoutSprint);
    }

    @Test
    public void itShouldCountIssuesByProject() {
        // given a project
        project = projectRepository.save(project);

        // issues count should be 0 at first
        assertThat(issueRepository.countByProjectId(project.getId())).isEqualTo(0);

        // given a list of issues
        List<Issue> issues = (List<Issue>) issueRepository.saveAll(
                List.of(
                        Issue.builder().project(project).summary("issue 1").build(),
                        Issue.builder().project(project).summary("issue 2").build(),
                        Issue.builder().project(project).summary("issue 3").build()
                )
        );

        // when the repository is invoked to fetch the issues count
        final int numberOfIssues = issueRepository.countByProjectId(project.getId());

        // then the number of issues in a given project should be returned
        assertThat(numberOfIssues).isEqualTo(issues.size());
    }

    @Test
    public void givenCountIssuesByProject_itShouldCountOnlyIssuesOfSpecifiedProjectById() {
        // given two distinct projects
        final Project project1 = projectRepository.save(Project.builder().name("project 01").build());
        final Project project2 = projectRepository.save(Project.builder().name("project 02").build());

        // given the first project issues
        List<Issue> project1Issues = (List<Issue>) issueRepository.saveAll(
                List.of(
                        Issue.builder().project(project1).summary("issue 11").build(),
                        Issue.builder().project(project1).summary("issue 12").build()
                )
        );

        // given the second project issues
        List<Issue> project2Issues = (List<Issue>) issueRepository.saveAll(
                List.of(
                        Issue.builder().project(project2).summary("issue 21").build(),
                        Issue.builder().project(project2).summary("issue 22").build(),
                        Issue.builder().project(project2).summary("issue 23").build()
                )
        );

        // when the repository is invoked to count project issues
        final int project1Count = issueRepository.countByProjectId(project1.getId());
        final int project2Count = issueRepository.countByProjectId(project2.getId());

        // then expect each project count to be correct
        assertThat(project1Count).isEqualTo(project1Issues.size());
        assertThat(project2Count).isEqualTo(project2Issues.size());
    }

    @Test
    public void itShouldSwapThePositionsOfTwoIssues() {
        // given the issues
        issue1 = issueRepository.save(issue1);
        issue2 = issueRepository.save(issue2);

        // given the issues' initial positions
        final int position1 = issue1.getPosition();
        final int position2 = issue2.getPosition();

        // when the repository is invoked to swap their positions
        issueRepository.swapPositions(issue1.getId(), issue2.getId());

        // then expect the issues' positions to have been swapped
        issue1 = issueRepository.findById(issue1.getId()).get();
        issue2 = issueRepository.findById(issue2.getId()).get();

        assertThat(issue1.getPosition()).isEqualTo(position2);
        assertThat(issue2.getPosition()).isEqualTo(position1);
    }
}
