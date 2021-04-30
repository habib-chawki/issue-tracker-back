package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.habibInc.issueTracker.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = IssueDto.class)
public class IssueDto {

    private Long id;

    private String description;
    private String summary;

    private IssueType type;
    private IssueStatus status;
    private IssuePriority priority;

    private int points;

    @JsonIdentityReference(alwaysAsId = true)
    private UserDto assignee;

    private UserDto reporter;
}
