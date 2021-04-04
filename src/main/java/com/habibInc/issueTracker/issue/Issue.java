package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.comment.Comment;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.sprint.Sprint;
import com.habibInc.issueTracker.user.User;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties(value = {"column", "reporter", "sprint", "project"})
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    private String IssueKey;

    private String description;
    private String summary;

    @Enumerated(EnumType.STRING)
    private IssueType type = IssueType.STORY;

    @Enumerated(EnumType.STRING)
    private IssueStatus status = IssueStatus.UNRESOLVED;

    @Enumerated(EnumType.STRING)
    private IssuePriority priority = IssuePriority.MEDIUM;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    private List<Comment> comments;

    private int votes;

    @OneToOne
    private User assignee;

    @OneToOne
    private User reporter;

    @ManyToOne
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    private Sprint sprint;

    @ManyToOne
    private Column column;

    private int points;

    private LocalDateTime creationTime;
    private LocalDateTime updateTime;

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return Objects.equals(id, issue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
