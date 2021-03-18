package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.board.Board;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SprintBoardDto {
    private Long id;

    private String name;
    private String goal;
    private SprintStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    private Board board;
}
