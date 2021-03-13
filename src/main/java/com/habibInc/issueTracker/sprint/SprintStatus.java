package com.habibInc.issueTracker.sprint;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SprintStatus {
    @JsonProperty("Active") ACTIVE,
    @JsonProperty("Inactive") INACTIVE,
    @JsonProperty("Over") OVER,
}
