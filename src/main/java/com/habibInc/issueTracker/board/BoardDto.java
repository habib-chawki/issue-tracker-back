package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.sprint.SprintBacklogDto;
import com.habibInc.issueTracker.user.UserDto;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class BoardDto {
    private Long id;
    private String name;
    private List<Column> columns;
    private UserDto owner;
    private SprintBacklogDto sprint;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardDto boardDto = (BoardDto) o;
        return Objects.equals(id, boardDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
