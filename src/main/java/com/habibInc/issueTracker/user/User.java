package com.habibInc.issueTracker.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.habibInc.issueTracker.issue.Issue;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;
import java.util.Objects;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Builder

@JsonIgnoreProperties(value = {"assignedIssues", "reportedIssues"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Email(message = "Email should be valid")
    private String email;

    private String firstName;
    private String lastName;
    private String userName;
    private String password;

    @OneToMany(mappedBy = "assignee")
    private List<Issue> assignedIssues;

    @OneToMany(mappedBy = "reporter")
    private List<Issue> reportedIssues;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
