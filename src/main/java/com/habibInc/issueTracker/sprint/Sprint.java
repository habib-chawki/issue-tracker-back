package com.habibInc.issueTracker.sprint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.habibInc.issueTracker.board.Board;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.project.Project;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Builder

@JsonIgnoreProperties({"project", "backlog"})
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String goal;

    @Enumerated(EnumType.STRING)
    private SprintStatus status = SprintStatus.INACTIVE;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @OneToMany(mappedBy = "sprint", fetch = FetchType.EAGER)
    private List<Issue> backlog;

    @OneToOne(mappedBy = "sprint")
    private Board board;

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
