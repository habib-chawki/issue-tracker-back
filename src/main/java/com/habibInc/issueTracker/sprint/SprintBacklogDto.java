package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.issue.Issue;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
public class SprintBacklogDto {
    private Long id;

    private String name;
    private String goal;
    private SprintStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    private List<Issue> backlog;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SprintBacklogDto that = (SprintBacklogDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
