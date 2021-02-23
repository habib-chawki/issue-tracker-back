package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/sprints")
public class SprintController {

    private final SprintService sprintService;

    @Autowired
    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Sprint createSprint(@RequestBody Sprint sprint) {
        return sprintService.createSprint(sprint);
    }

    @GetMapping("/{sprintId}")
    @ResponseStatus(HttpStatus.OK)
    public Sprint getSprintById(@PathVariable Long sprintId){
        return sprintService.getSprintById(sprintId);
    }

    @PostMapping("/{sprintId}/issues")
    @ResponseStatus(HttpStatus.OK)
    public void setSprintIssues(@PathVariable Long sprintId, @RequestBody List<Issue> issues){
        sprintService.setSprintIssues(sprintId, issues);
    }

}
