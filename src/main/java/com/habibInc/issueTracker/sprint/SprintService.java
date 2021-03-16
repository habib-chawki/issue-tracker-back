package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprintService {

    private final SprintRepository sprintRepository;
    private final IssueRepository issueRepository;
    private final ProjectService projectService;

    @Autowired
    public SprintService(SprintRepository sprintRepository, IssueRepository issueRepository, ProjectService projectService) {
        this.sprintRepository = sprintRepository;
        this.issueRepository = issueRepository;
        this.projectService = projectService;
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
            moveUnfinishedIssuesToProductBacklog(sprint);
        }

        // update the sprint status
        sprint.setStatus(status);

        return sprintRepository.save(sprint);
    }

    public void moveUnfinishedIssuesToProductBacklog(Sprint sprint) {
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
}
