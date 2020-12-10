package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.security.CustomUserDetails;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
                             @AuthenticationPrincipal User authenticatedUser) {
        return issueService.createIssue(issue, authenticatedUser);
    }

    @GetMapping("/{id}")
    public Issue getIssue(@PathVariable String id){
        try {
            Long issueId = Long.parseLong(id);
            return issueService.getIssue(issueId);
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
    public Issue updateIssue(@PathVariable String id, @RequestBody Issue issue) {
        return issueService.updateIssue(issue);
    }
}
