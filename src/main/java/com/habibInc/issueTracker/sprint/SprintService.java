package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SprintService {

    private final SprintRepository sprintRepository;

    @Autowired
    public SprintService(SprintRepository sprintRepository) {
        this.sprintRepository = sprintRepository;
    }

    public Sprint createSprint(Sprint sprint) {
        return sprintRepository.save(sprint);
    }

    public void addIssues(List<Issue> issues) {

    }
}
