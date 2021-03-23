package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IssueResolution {
    @JsonProperty(value = "Unresolved") UNRESOLVED,
    @JsonProperty(value = "In Progress") IN_PROGRESS,
    @JsonProperty(value = "Resolved") RESOLVED
}
