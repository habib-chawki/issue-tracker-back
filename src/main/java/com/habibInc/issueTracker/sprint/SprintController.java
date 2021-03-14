package com.habibInc.issueTracker.sprint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}/sprints")
public class SprintController {

    private final SprintService sprintService;

    @Autowired
    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Sprint createSprint(@PathVariable Long projectId, @RequestBody Sprint sprint) {
        return sprintService.createSprint(projectId, sprint);
    }

    @GetMapping("/{sprintId}")
    @ResponseStatus(HttpStatus.OK)
    public Sprint getSprintById(@PathVariable Long sprintId){
        return sprintService.getSprintById(sprintId);
    }

    @PatchMapping("/{sprintId}/backlog")
    @ResponseStatus(HttpStatus.OK)
    public int setSprintBacklog(@PathVariable("sprintId") String id, @RequestBody List<Long> issuesIds){
        Long sprintId = Utils.validateId(id);
        return sprintService.setSprintBacklog(sprintId, issuesIds);
    }

    @GetMapping(value = "", params = "status")
    @ResponseStatus(HttpStatus.OK)
    public List<Sprint> getSprintsByStatus(@RequestParam SprintStatus status) {
        return sprintService.getSprintsByStatus(status);
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
}
