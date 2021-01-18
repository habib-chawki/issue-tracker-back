package com.habibInc.issueTracker.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BoardServiceTest {
    @InjectMocks
    BoardService boardService;

    @Mock
    BoardRepository boardRepository;

    Board board;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        board = new Board();
        board.setName("Kanban");
    }

    @Test
    public void itShouldCreateBoard() {
        // given the board repository's "save()" method
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        // when "createBoard()" service method is invoked
        Board createdBoard = boardService.createBoard(board);

        // then the response should be the successfully created board
        assertThat(createdBoard).isEqualTo(board);
    }
}
