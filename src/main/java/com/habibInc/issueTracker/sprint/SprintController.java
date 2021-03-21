package com.habibInc.issueTracker.sprint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public Sprint createSprint(@PathVariable Long projectId, @RequestBody Sprint sprint) {
        return sprintService.createSprint(projectId, sprint);
    }

    @GetMapping("/{sprintId}")
    @ResponseStatus(HttpStatus.OK)
    public SprintBoardDto getSprintById(@PathVariable Long sprintId){
        Sprint sprint = sprintService.getSprintById(sprintId);

        // convert Sprint to SprintBoardDto
        SprintBoardDto sprintDto = modelMapper.map(sprint, SprintBoardDto.class);

        return sprintDto;
    }

    @PatchMapping("/{sprintId}/backlog")
    @ResponseStatus(HttpStatus.OK)
    public int setSprintBacklog(@PathVariable("sprintId") String id, @RequestBody List<Long> issuesIds){
        Long sprintId = Utils.validateId(id);
        return sprintService.setSprintBacklog(sprintId, issuesIds);
    }

    @GetMapping(value = "", params = "status")
    @ResponseStatus(HttpStatus.OK)
    public List<SprintBacklogDto> getSprintsByStatus(@RequestParam SprintStatus status) {
        List<Sprint> sprints = sprintService.getSprintsByStatus(status);

        // convert each sprint to SprintBacklogDto
        List<SprintBacklogDto> sprintsByStatus = sprints.stream()
                .map(sprint -> modelMapper.map(sprint, SprintBacklogDto.class))
                .collect(Collectors.toList());

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
        return sprintService.updateSprintStatus(sprintId, status);
    }

    @PatchMapping("{sprintId}/issues/{issueId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateIssueSprint(@RequestBody String request,
                                  @PathVariable Long sprintId,
                                  @PathVariable Long issueId) throws JsonProcessingException {
        // extract request body
        Map<String, String> requestBody = new ObjectMapper().readValue(request, Map.class);

        // extract new sprint id (null indicates the issue is back to the product backlog)
        Long newSprintId = null;

        if(!requestBody.get("newSprintId").equals("null")){
            newSprintId = Utils.validateId(requestBody.get("newSprintId"));
        }

        // update issue sprint
        sprintService.updateIssueSprint(sprintId, issueId, newSprintId);
    }
}
