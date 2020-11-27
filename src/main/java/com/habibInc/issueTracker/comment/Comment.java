package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Issue issue;

    @OneToOne(cascade = CascadeType.PERSIST)
    private User owner;

    private String content;

    private LocalDateTime creationTime;
    private LocalDateTime updateTime;


    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id.equals(comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
