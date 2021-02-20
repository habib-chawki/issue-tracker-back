package com.habibInc.issueTracker.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.user.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@JsonIgnoreProperties(value = {"owner"})
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
}
