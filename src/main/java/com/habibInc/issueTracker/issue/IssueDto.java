package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.comment.CommentDto;
import com.habibInc.issueTracker.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

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
    private List<CommentDto> comments;

    private UserDto assignee;
    private UserDto reporter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueDto issueDto = (IssueDto) o;
        return id.equals(issueDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
