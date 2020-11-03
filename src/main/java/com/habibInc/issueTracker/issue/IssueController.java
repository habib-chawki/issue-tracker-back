package com.habibInc.issueTracker.issue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Issue> createIssue(){
        return new ResponseEntity<Issue>(new Issue(2L), HttpStatus.CREATED);
    }
}
