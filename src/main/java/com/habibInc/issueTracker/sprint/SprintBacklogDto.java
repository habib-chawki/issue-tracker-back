package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.issue.Issue;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SprintBacklogDto {
    private Long id;
    private String name;
    private String goal;
    private SprintStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Issue> backlog;
}
