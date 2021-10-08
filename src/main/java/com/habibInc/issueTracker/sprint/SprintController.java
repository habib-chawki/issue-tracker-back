package com.habibInc.issueTracker.sprint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.utils.validation.IdValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/projects/{projectId}/sprints")
public class SprintController {

    private final SprintService sprintService;
    private final ModelMapper modelMapper;

    @Autowired
    public SprintController(SprintService sprintService, ModelMapper modelMapper) {
        this.sprintService = sprintService;
        this.modelMapper = modelMapper;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public SprintBacklogDto createSprint(@PathVariable Long projectId, @RequestBody Sprint sprint) {
        Sprint createdSprint = sprintService.createSprint(projectId, sprint);

        // convert Sprint to SprintBacklogDto
        SprintBacklogDto sprintDto = modelMapper.map(createdSprint, SprintBacklogDto.class);

        log.info("Created sprint: {}", sprintDto);

        return sprintDto;
    }

    @GetMapping("/{sprintId}")
    @ResponseStatus(HttpStatus.OK)
    public SprintBoardDto getSprintById(@PathVariable Long sprintId){
        Sprint sprint = sprintService.getSprintById(sprintId);

        // convert Sprint to SprintBoardDto
        SprintBoardDto sprintDto = modelMapper.map(sprint, SprintBoardDto.class);

        log.info("Fetched sprint: {}", sprintDto);

        return sprintDto;
    }

    @PatchMapping("/{sprintId}/backlog")
    @ResponseStatus(HttpStatus.OK)
    public int setSprintBacklog(@PathVariable("sprintId") String id, @RequestBody List<Long> issuesIds){
        Long sprintId = IdValidator.validate(id);
        final int backlog = sprintService.setSprintBacklog(sprintId, issuesIds);
        log.info("Set sprint backlog: {sprintId: {}, issues: {}}", id, issuesIds);
        return backlog;
    }

    @GetMapping(value = "", params = "status")
    @ResponseStatus(HttpStatus.OK)
    public List<SprintBacklogDto> getSprintsByStatus(@RequestParam SprintStatus status) {
        List<Sprint> sprints = sprintService.getSprintsByStatus(status);

        // convert each sprint to SprintBacklogDto
        List<SprintBacklogDto> sprintsByStatus = sprints.stream()
                .map(sprint -> modelMapper.map(sprint, SprintBacklogDto.class))
                .collect(Collectors.toList());

        log.info("Fetched sprints by status: {}", sprintsByStatus);

        return sprintsByStatus;
    }

    @PatchMapping("{sprintId}")
    @ResponseStatus(HttpStatus.OK)
    public Sprint updateSprintStatus(@PathVariable Long sprintId, @RequestBody String request) throws JsonProcessingException {
        // extract request body
        Map<String, String> requestBody = new ObjectMapper().readValue(request, Map.class);

        // extract new sprint status
        SprintStatus status = SprintStatus.valueOf(requestBody.get("newSprintStatus").toUpperCase());

        // update sprint status
        final Sprint updatedSprint = sprintService.updateSprintStatus(sprintId, status);
        log.info("Updated sprint status: {sprintId: {}, status: {}}", sprintId, status);
        return updatedSprint;
    }

    @PatchMapping("{sprintId}/issues/{issueId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateIssueSprint(@RequestBody String request,
                                  @PathVariable String sprintId,
                                  @PathVariable Long issueId) throws JsonProcessingException {
        // extract request body
        Map<String, String> requestBody = new ObjectMapper().readValue(request, Map.class);

        // extract new sprint id (null indicates the issue is back to the product backlog)
        Long newSprintId = null;

        if(!requestBody.get("newSprintId").equals("null")){
            newSprintId = IdValidator.validate(requestBody.get("newSprintId"));
        }

        // update issue sprint
        sprintService.updateIssueSprint(sprintId, issueId, newSprintId);

        log.info("Updated issue sprint: {issueId: {}, newSprintId: {}}", issueId, newSprintId);
    }

    @DeleteMapping("{sprintId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSprintById(@PathVariable Long projectId, @PathVariable Long sprintId, @AuthenticationPrincipal User authenticatedUser) {
        // invoke service, delete sprint by id
        sprintService.deleteSprintById(projectId, sprintId, authenticatedUser);
    }
}
