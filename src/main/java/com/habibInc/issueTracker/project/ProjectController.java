package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueDto;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.utils.validation.IdValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
        final Project createdProject = projectService.createProject(project, authenticatedUser);
        log.info("Project created: {}", createdProject);
        return createdProject;
    }

    @GetMapping(value = {"", "/"}, params = "user")
    @ResponseStatus(HttpStatus.OK)
    public Set<Project> getProjectsByAssignedUser(@RequestParam("user") Long userId) {
        final Set<Project> projectsByAssignedUser = projectService.getProjectsByAssignedUser(userId);
        log.info("Fetched projects by assigned user: {userId: {}, projects: {}}", userId, projectsByAssignedUser);
        return projectsByAssignedUser;
    }

    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<Project> getProjects(){
        final List<Project> projects = projectService.getProjects();
        log.info("Fetched all projects: {}", projects);
        return projects;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Project getProject(@PathVariable String id) {
        Long parsedId = IdValidator.validate(id);
        final Project projectById = projectService.getProjectById(parsedId);
        log.info("Fetched project by id: {}", projectById);
        return projectById;
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

        log.info("Fetched backlog: {projectId: {}, backlog: {}}", id, backlog);

        return backlog;
    }

    @PostMapping("/{projectId}/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addUserToProject(@PathVariable("projectId") Long projectId,
                                 @PathVariable("userId") Long userId){
        projectService.addUserToProject(userId, projectId);

        log.info("Added user to project: {userId: {}, projectId: {}}", userId, projectId);
    }

    @DeleteMapping("/{projectId}/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserFromProject(@PathVariable("projectId") Long projectId,
                                      @PathVariable("userId") Long userId) {
        projectService.removeUserFromProject(userId, projectId);

        log.info("Removed user from project: {userId: {}, projectId: {}}", userId, projectId);
    }
}
