package com.habibInc.issueTracker.sprint;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/sprints")
public class SprintController {

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Sprint createSprint(@RequestBody Sprint sprint) {
        return sprint;
    }

}
