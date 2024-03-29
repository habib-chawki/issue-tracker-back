package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.issue.IssueDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class SprintBacklogDto {
    private Long id;

    private String name;
    private String goal;
    private SprintStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    private List<IssueDto> backlog;

    @Override
    public String toString() {
        return "{" +
                "id: " + id +
                ", name: '" + name + '\'' +
                ", goal: '" + goal + '\'' +
                ", status: " + status +
                ", startDate: " + startDate +
                ", endDate: " + endDate +
                ", backlog: " + backlog +
                '}';
    }

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
