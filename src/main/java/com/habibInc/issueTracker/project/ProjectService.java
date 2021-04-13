package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final UserService userService;

    @Autowired
    ProjectService(ProjectRepository projectRepository, IssueRepository issueRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.issueRepository = issueRepository;
        this.userService = userService;
    }

    public Project createProject(Project project, User authenticatedUser) {
        // set project owner
        project.setOwner(authenticatedUser);

        // add owner to list of assigned users
        project.setAssignedUsers(Set.of(authenticatedUser));

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
        return issueRepository.findAllByProjectIdAndSprintId(projectId, null);
    }

    public Set<Project> getProjectsByAssignedUser(Long userId) {
        return projectRepository.findAllByAssignedUsersId(userId);
    }

    public void addUserToProject(Long userId, Long projectId) {
        // fetch user by id (throws user not found error)
        userService.getUserById(userId);

        // fetch project by id (throws project not found error)
        getProjectById(projectId);

        // add to user to project in case both user and project exist
        projectRepository.addUserToProject(userId, projectId);
    }
}
