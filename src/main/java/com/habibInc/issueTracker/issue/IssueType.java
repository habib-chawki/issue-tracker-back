package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IssueType {
    @JsonProperty(value = "Story") STORY,
    @JsonProperty(value = "Bug") BUG,
    @JsonProperty(value = "Task") TASK
}
