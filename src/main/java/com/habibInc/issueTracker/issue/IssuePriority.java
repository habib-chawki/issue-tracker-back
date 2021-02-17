package com.habibInc.issueTracker.issue;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IssuePriority {
    @JsonProperty(value = "Low") LOW,
    @JsonProperty(value = "Medium") MEDIUM,
    @JsonProperty(value = "High") HIGH
}
