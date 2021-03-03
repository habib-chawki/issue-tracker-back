package com.habibInc.issueTracker.sprint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.habibInc.issueTracker.board.Board;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.project.Project;
import lombok.*;

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

public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String goal;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @OneToMany(mappedBy = "sprint", fetch = FetchType.EAGER)
    private List<Issue> backlog;

    @OneToOne(cascade = CascadeType.ALL)
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
