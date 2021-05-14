package com.habibInc.issueTracker.user;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.utils.validation.UniqueEmail;
import com.habibInc.issueTracker.utils.validation.UniqueUsername;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    @Column(unique = true, nullable = false, updatable = false)
    private Long id;

    @Email(message = "Email should be valid")
    @UniqueEmail(message = "Email is already registered")
    @NotBlank(message = "Email must not be blank")
    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Size(min = 5, message = "Password should be at least 5 characters long")
    @NotBlank(message = "Password must not be blank")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    @UniqueUsername(message = "Username is already in use")
    private String username;

    @OneToMany(mappedBy = "assignee")
    private List<Issue> assignedIssues;

    @OneToMany(mappedBy = "reporter")
    private List<Issue> reportedIssues;

    @OneToMany(mappedBy = "owner")
    private List<Project> createdProjects;

    @ManyToMany(mappedBy = "assignedUsers")
    private Set<Project> assignedProjects;

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
