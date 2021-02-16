package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.comment.Comment;
import com.habibInc.issueTracker.user.User;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties(value = {"column", "reporter", "assignee"})
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    private String IssueKey;

    private String description;
    private String summary;

    private IssueType type;
    private IssueResolution resolution;

    @OneToMany(mappedBy = "issue", cascade = CascadeType.ALL)
    private List<Comment> comments;

    private int votes;

    @OneToOne(cascade = CascadeType.PERSIST)
    private User assignee;

    @OneToOne
    private User reporter;

    private LocalDateTime creationTime;
    private LocalDateTime updateTime;

    private String estimate;

    @ManyToOne
    private Column column;

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
