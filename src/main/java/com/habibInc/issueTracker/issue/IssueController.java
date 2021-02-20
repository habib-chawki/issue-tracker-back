package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;
    private final UserService userService;

    @Autowired
    public IssueController(IssueService issueService, UserService userService) {
        this.issueService = issueService;
        this.userService = userService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Issue createIssue(@RequestBody Issue issue,
                             @AuthenticationPrincipal User authenticatedUser,
                             @RequestParam(name = "project") Long projectId) {
        return issueService.createIssue(issue, authenticatedUser, projectId);
    }

    @GetMapping("/{id}")
    public Issue getIssue(@PathVariable String id){
        try {
            Long issueId = Long.parseLong(id);
            return issueService.getIssueById(issueId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid issue id");
        }
    }

    @GetMapping({"", "/"})
    public Iterable<Issue> getAllIssues(){
        return issueService.getAllIssues();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Issue updateIssue(@PathVariable String id,
                             @RequestBody Issue issue,
                             @AuthenticationPrincipal User authenticatedUser) {
        try{
            Long issueId = Long.parseLong(id);
            return issueService.updateIssue(issueId, issue, authenticatedUser);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid issue id");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteIssue(@PathVariable String id,
                            @AuthenticationPrincipal User authenticatedUser){
        try{
            Long issueId = Long.parseLong(id);
            issueService.deleteIssue(issueId, authenticatedUser);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid issue id");
        }
    }
}
