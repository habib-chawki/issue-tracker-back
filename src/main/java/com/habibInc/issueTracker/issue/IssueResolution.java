package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IssueResolution {
    @JsonProperty(value = "Unresolved") UNRESOLVED,
    @JsonProperty(value = "Done") DONE,
    @JsonProperty(value = "Duplicate") DUPLICATE,
    @JsonProperty(value = "Wont Do") WONT_DO
}
