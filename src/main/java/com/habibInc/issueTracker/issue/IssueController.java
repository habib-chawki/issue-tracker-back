package com.habibInc.issueTracker.issue;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issues")
public class IssueController {

    @GetMapping("/{id}")
    public Issue getIssue(@PathVariable Long id){
        return new Issue(1L);
    }
}
