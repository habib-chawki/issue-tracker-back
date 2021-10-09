package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.issue.IssueService;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectService;
import com.habibInc.issueTracker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprintService {

    private final ProjectService projectService;
    private final SprintRepository sprintRepository;
    private final IssueService issueService;
    private final IssueRepository issueRepository;

    @Autowired
    public SprintService(ProjectService projectService, SprintRepository sprintRepository, IssueService issueService, IssueRepository issueRepository) {
        this.projectService = projectService;
        this.sprintRepository = sprintRepository;
        this.issueService = issueService;
        this.issueRepository = issueRepository;
    }

    public Sprint createSprint(Long projectId, Sprint sprint) {
        // find the project (throws project not found id)
        Project project = projectService.getProjectById(projectId);

        // set the sprint project before saving
        sprint.setProject(project);
        return sprintRepository.save(sprint);
    }

    public Sprint getSprintById(Long sprintId) {
        return sprintRepository.findById(sprintId).orElseThrow(() ->
                new ResourceNotFoundException("Sprint not found"));
    }

    public int setSprintBacklog(Long sprintId, List<Long> issuesIds) {
        return issueRepository.updateIssuesSprint(sprintId, issuesIds);
    }

    public List<Sprint> getSprintsByStatus(SprintStatus status) {
        return sprintRepository.findAllByStatus(status);
    }

    public Sprint updateSprintStatus(Long sprintId, SprintStatus status) {
        // find the sprint by id (throws sprint not found error)
        Sprint sprint = getSprintById(sprintId);

        // when the sprint is over, then return the unfinished issues to the product backlog
        if(status.equals(SprintStatus.OVER)){
            moveUnfinishedSprintIssuesToProductBacklog(sprint);
        }

        // update the sprint status
        sprint.setStatus(status);

        return sprintRepository.save(sprint);
    }

    public void moveUnfinishedSprintIssuesToProductBacklog(Sprint sprint) {
        List<Issue> sprintBacklog = sprint.getBacklog();
        List<Column> boardColumns = sprint.getBoard().getColumns();

        Long lastColumnId = boardColumns.get(boardColumns.size() - 1).getId();

        // extract the id of all the issues that do not belong to the last column
        List<Long> unfinishedIssues = sprintBacklog.stream()
                .filter((issue) -> !issue.getColumn().getId().equals(lastColumnId))
                .map((issue) -> issue.getId())
                .collect(Collectors.toList());

        // move the unfinished issues back to the product backlog (set the sprint to null)
        issueRepository.updateIssuesSprint(null, unfinishedIssues);
    }

    public void updateIssueSprint(String sprintId, Long issueId, Long newSprintId) {
        // check if the id is null (indicates that the issue is back to the product backlog)
        // otherwise fetch the new sprint by id (throws sprint not found error)
        Sprint newSprint = newSprintId != null ? getSprintById(newSprintId) : null;

        // get the issue (throws issue not found error)
        Issue issue = issueService.getIssueById(issueId);

        // update the sprint
        issue.setSprint(newSprint);

        //save the sprint
        issueRepository.save(issue);
    }

    public void deleteSprintById(Long projectId, Long sprintId, User authenticatedUser) {
        final Project project = projectService.getProjectById(projectId);
        final Sprint sprintToDelete = getSprintById(sprintId);

        // delete sprint only when authenticated user is the project owner
        if(!project.getOwner().equals(authenticatedUser)) {
            throw new ForbiddenOperationException("Unauthorized to delete sprint");
        }

        // move sprint issues back to product backlog
        moveIssuesToProductBacklog(sprintToDelete.getBacklog());

        // invoke repository, delete sprint by id
        sprintRepository.deleteById(sprintToDelete.getId());
    }

    private void moveIssuesToProductBacklog(List<Issue> issues) {
        // extract issues' ids
        final List<Long> issuesIds = issues.stream()
                .map(issue -> issue.getId())
                .collect(Collectors.toList());

        // move the issues back to the product backlog (set their sprint property to null)
        issueRepository.updateIssuesSprint(null, issuesIds);
    }
}
