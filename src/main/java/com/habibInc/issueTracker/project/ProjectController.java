package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueDto;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.utils.validation.IdValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProjectController(ProjectService projectService, ModelMapper modelMapper) {
        this.projectService = projectService;
        this.modelMapper = modelMapper;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@Valid @RequestBody Project project,
                                 @AuthenticationPrincipal User authenticatedUser) {
        return projectService.createProject(project, authenticatedUser);
    }

    @GetMapping(value = {"", "/"}, params = "user")
    @ResponseStatus(HttpStatus.OK)
    public Set<Project> getProjectsByAssignedUser(@RequestParam("user") Long userId) {
        return projectService.getProjectsByAssignedUser(userId);
    }

    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<Project> getProjects(){
        return projectService.getProjects();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Project getProject(@PathVariable String id) {
        Long parsedId = IdValidator.validate(id);
        return projectService.getProjectById(parsedId);
    }

    @GetMapping("/{id}/backlog")
    @ResponseStatus(HttpStatus.OK)
    public List<IssueDto> getBacklog(@PathVariable String id) {
        // validate project id
        Long projectId = IdValidator.validate(id);

        // fetch list of issues by project id
        List<Issue> issues = projectService.getBacklog(projectId);

        // map to issue DTOs
        final List<IssueDto> backlog = issues.stream()
                .map((issue) -> modelMapper.map(issue, IssueDto.class))
                .collect(Collectors.toList());

        return backlog;
    }

    @PostMapping("/{projectId}/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addUserToProject(@PathVariable("projectId") Long projectId,
                                 @PathVariable("userId") Long userId){
        projectService.addUserToProject(userId, projectId);
    }

    @DeleteMapping("/{projectId}/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserFromProject(@PathVariable("projectId") Long projectId,
                                      @PathVariable("userId") Long userId) {
        projectService.removeUserFromProject(userId, projectId);
    }
}
