package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectService;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public IssueService(IssueRepository issueRepository, ProjectService projectService, UserService userService) {
        this.issueRepository = issueRepository;
        this.projectService = projectService;
        this.userService = userService;
    }

    public Issue getIssueById(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
    }

    public Issue createIssue(Issue issue, User authenticatedUser, Long projectId) {
        // find the project by id (throws project not found exception)
        Project project = projectService.getProjectById(projectId);

        // set the issue project and reporter
        issue.setReporter(authenticatedUser);
        issue.setProject(project);

        // set the creation time
        issue.setCreationTime(LocalDateTime.now());

        // get the project issues count to determine the new issue's position
        final int position = issueRepository.countByProjectId(projectId) + 1;

        // set the issue's position
        issue.setPosition(position);

        // save the issue
        return issueRepository.save(issue);
    }

    public Iterable<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public Issue updateIssue(Long issueId, Issue issue, User authenticatedUser) {
        // make sure the issue already exists, otherwise an exception is thrown
        Issue issueToUpdate = getIssueById(issueId);

        // save the issue only if authenticated user is the reporter
        if (issueToUpdate.getReporter().equals(authenticatedUser))
        {
            // set the update time
            issue.setUpdateTime(LocalDateTime.now());
            return issueRepository.save(issue);
        }

        // in case the authenticated user is not the reporter, throw a forbidden error
        throw new ForbiddenOperationException("Forbidden");
    }

    public void deleteIssue(Long issueId, User authenticatedUser) {
        Issue issueToDelete = getIssueById(issueId);

        // in case the authenticated user is not the reporter, throw a forbidden error
        if (issueToDelete.getReporter().equals(authenticatedUser))
            issueRepository.deleteById(issueId);
        else
            throw new ForbiddenOperationException("Forbidden");
    }

    public Issue updateIssueAssignee(Long issueId, Long userId) {
        // fetch the user by id (throws user not found error)
        User assignee = userService.getUserById(userId);

        // fetch the issue by id (throws issue not found error)
        Issue issue = getIssueById(issueId);

        // set the assignee
        issue.setAssignee(assignee);

        return issue;
    }

    @Transactional
    public void swapIssuesPositions(Long projectId, Long issueId1, Long issueId2) {
        // fetch the project by id
        final Project project = projectService.getProjectById(projectId);

        // fetch the issues by id
        final Issue issue1 = getIssueById(issueId1);
        final Issue issue2 = getIssueById(issueId2);

        // assert that the issues belong to the same project
        if(!issue1.getProject().equals(project) || !issue2.getProject().equals(project)){
            throw new ForbiddenOperationException("Can not swap issues");
        }

        // swap issues positions
        final int position1 = issue1.getPosition();
        final int position2 = issue2.getPosition();

        issue1.setPosition(position2);
        issue2.setPosition(position1);
    }
}
