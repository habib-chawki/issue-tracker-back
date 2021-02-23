package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.project.Project;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Builder

public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String goal;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne
    private Project project;

    @OneToMany(mappedBy = "sprint", fetch = FetchType.EAGER)
    private List<Issue> issues;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprint sprint = (Sprint) o;
        return id.equals(sprint.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
