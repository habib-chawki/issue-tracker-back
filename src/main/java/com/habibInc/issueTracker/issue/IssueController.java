package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public IssueController(IssueService issueService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.issueService = issueService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = {"", "/"}, params = "project")
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

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IssueDto updateIssueAssignee(@PathVariable("id") Long issueId,
                                        @RequestBody String request) throws JsonProcessingException {
        // extract the new assignee id from the request body
        Map<String, String> map = objectMapper.readValue(request, Map.class);
        Long assigneeId = Utils.validateId(map.get("assignee"));

        // invoke service, update assignee
        Issue issue = issueService.updateIssueAssignee(issueId, assigneeId);

        // convert to IssueDto
        IssueDto updatedIssue = modelMapper.map(issue, IssueDto.class);

        return updatedIssue;
    }
}
