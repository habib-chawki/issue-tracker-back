package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.board.Board;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
public class SprintBoardDto {
    private Long id;

    private String name;
    private String goal;
    private SprintStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    private Board board;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SprintBoardDto that = (SprintBoardDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}