package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.utils.validation.IdValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
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
    public IssueDto createIssue(@RequestBody Issue issue,
                             @AuthenticationPrincipal User authenticatedUser,
                             @RequestParam(name = "project") Long projectId) {
        // fetch and map the created issue
        Issue createdIssue = issueService.createIssue(issue, authenticatedUser, projectId);
        final IssueDto createdIssueDto = modelMapper.map(createdIssue, IssueDto.class);

        log.info("Issue created: {}", createdIssueDto);

        return createdIssueDto;
    }

    @GetMapping("/{id}")
    public Issue getIssue(@PathVariable String id){
        try {
            Long issueId = Long.parseLong(id);
            final Issue issueById = issueService.getIssueById(issueId);

            log.info("Fetched issue by id: {issueId: {}}", id);

            return issueById;
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
    public IssueDto updateIssue(@PathVariable String id,
                             @RequestBody Issue issue,
                             @AuthenticationPrincipal User authenticatedUser) {
        try{
            Long issueId = Long.parseLong(id);
            Issue updatedIssue = issueService.updateIssue(issueId, issue, authenticatedUser);

            // set and return issue dto
            final IssueDto updatedIssueDto = modelMapper.map(updatedIssue, IssueDto.class);

            log.info("Issue updated: {}", updatedIssueDto);

            return updatedIssueDto;
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

            log.info("Issue deleted by id: {issueId: {}}", id);
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
        Long assigneeId = IdValidator.validate(map.get("assignee"));

        // invoke service, update assignee
        Issue issue = issueService.updateIssueAssignee(issueId, assigneeId);

        // convert to IssueDto
        IssueDto updatedIssue = modelMapper.map(issue, IssueDto.class);

        log.info("Issue assignee updated: {newAssignee: {}, updatedIssue: {}}", assigneeId, updatedIssue);

        return updatedIssue;
    }

    @PatchMapping(params = "project")
    @ResponseStatus(HttpStatus.OK)
    public void swapIssuesPositions(@RequestParam("project") String id, @RequestBody JsonNode request) {
        // extract the two issues ids
        Long issueId1 = IdValidator.validate(request.get("issue1").toString());
        Long issueId2 = IdValidator.validate(request.get("issue2").toString());

        // validate project id
        Long projectId = IdValidator.validate(id);

        // invoke service, swap issues' positions
        issueService.swapIssuesPositions(projectId, issueId1, issueId2);
    }

}
