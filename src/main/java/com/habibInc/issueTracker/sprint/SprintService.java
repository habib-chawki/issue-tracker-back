package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return issueRepository.setSprintBacklog(sprintId, issuesIds);
    }

    public List<Sprint> getSprintsByStatus(SprintStatus status) {
        return sprintRepository.findAllByStatus(status);
    }

    public Sprint updateSprintStatus(Long sprintId, SprintStatus status) {
        return null;
    }
}
