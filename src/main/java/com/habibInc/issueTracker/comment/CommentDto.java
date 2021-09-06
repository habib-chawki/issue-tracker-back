package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CommentDto {

    private Long id;

    private String content;

    private Long issueId;
    private UserDto owner;

    private LocalDateTime creationTime;
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "{" +
                "id: " + id +
                ", content: '" + content + '\'' +
                ", issueId: " + issueId +
                ", owner: " + owner +
                ", creationTime: " + creationTime +
                ", updateTime: " + updateTime +
                '}';
    }
}
