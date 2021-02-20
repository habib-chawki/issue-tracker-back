package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;

    @Autowired
    ProjectService(ProjectRepository projectRepository, IssueRepository issueRepository) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
    }

    public Project createProject(Project project, User authenticatedUser) {
        project.setOwner(authenticatedUser);
        return projectRepository.save(project);
    }

    public List<Project> getProjects() {
        return (List<Project>) projectRepository.findAll();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public List<Issue> getBacklog(Long projectId) {
        return issueRepository.findAllByProjectId(projectId);
    }
}
