package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.habibInc.issueTracker.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class IssueDto {

    private Long id;

    private String description;
    private String summary;

    private IssueType type;
    private IssueStatus status;
    private IssuePriority priority;

    private int points;

    @JsonProperty("assignee")
    private Long assigneeId;

    @JsonProperty("reporter")
    private UserDto reporterId;
}
