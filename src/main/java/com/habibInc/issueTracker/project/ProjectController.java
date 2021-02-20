package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@Valid @RequestBody Project project,
                                 @AuthenticationPrincipal User authenticatedUser) {
        return projectService.createProject(project, authenticatedUser);
    }

    @GetMapping({"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public List<Project> getProjects(){
        return projectService.getProjects();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Project getProject(@PathVariable String id) {
        Long parsedId = Utils.validateId(id);
        return projectService.getProjectById(parsedId);
    }

    @GetMapping("/{id}/backlog")
    @ResponseStatus(HttpStatus.OK)
    public List<Issue> getBacklog(@PathVariable String id) {
        Long projectId = Utils.validateId(id);
        return projectService.getBacklog(projectId);
    }
}
