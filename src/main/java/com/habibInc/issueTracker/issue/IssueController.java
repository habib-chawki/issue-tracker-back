package com.habibInc.issueTracker.issue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issues")
public class IssueController {
    @Autowired
    IssueService issueService;

    @GetMapping("/{id}")
    public Issue getIssue(@PathVariable Long id){
        return issueService.getIssue(id);
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Issue createIssue(Issue issue){
        return issueService.createIssue(issue);
    }
}
