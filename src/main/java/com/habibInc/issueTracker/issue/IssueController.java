package com.habibInc.issueTracker.Issue;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/issues")
public class IssueController {

    @GetMapping("/{id}")
    public Issue getIssue(@PathVariable Long id){
        return new Issue(1L);
    }
}
