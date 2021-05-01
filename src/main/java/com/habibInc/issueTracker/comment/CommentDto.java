package com.habibInc.issueTracker.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;

    @JsonProperty("issue")
    private Long issueId;

    @JsonProperty("owner")
    private Long ownerId;

    private String content;

    private LocalDateTime creationTime;
    private LocalDateTime updateTime;

}
