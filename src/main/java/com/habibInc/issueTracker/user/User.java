package com.habibInc.issueTracker.user;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.project.Project;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Builder

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties(value = {"projects", "assignedIssues", "reportedIssues"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 5, message = "Password should be at least 5 characters long")
    private String password;

    private String fullName;
    private String userName;

    @OneToMany(mappedBy = "assignee")
    private List<Issue> assignedIssues;

    @OneToMany(mappedBy = "reporter")
    private List<Issue> reportedIssues;

    @OneToMany(mappedBy = "owner")
    private List<Project> projects;

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
