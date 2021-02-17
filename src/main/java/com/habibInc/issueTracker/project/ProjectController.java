package com.habibInc.issueTracker.project;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@RequestBody Project project){
        return project;
    }

}
