package com.habibInc.issueTracker.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.sprint.Sprint;
import com.habibInc.issueTracker.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@JsonIgnoreProperties(value = {"owner", "backlog", "sprints", "assignedUsers"})
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "project")
    private List<Issue> backlog;

    @OneToOne
    private User owner;

    @OneToMany(mappedBy = "project")
    private List<Sprint> sprints;

    @ManyToMany
    @JoinTable(
            name = "project_user",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedUsers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id.equals(project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
