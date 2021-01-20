package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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

        board.setId(1L);
        board.setName("Kanban");
    }

    @Test
    public void itShouldCreateBoard() {
        // given the board repository "save()" method
        when(boardRepository.save(board)).thenReturn(board);

        // when "createBoard()" service method is invoked
        Board createdBoard = boardService.createBoard(board);

        // then the response should be the successfully created board
        assertThat(createdBoard).isEqualTo(board);
    }

    @Test
    public void itShouldGetBoardById() {
        // given the board repository
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        // when "getBoardById()" service method is invoked
        Board retrievedBoard = boardService.getBoardById(board.getId());

        // then the board should be retrieved successfully
        assertThat(retrievedBoard).isEqualTo(board);
    }

    @Test
    public void givenGetBoardById_whenBoardDoesNotExist_itShouldReturnNotFoundError() {
        // when the board is not found
        when(boardRepository.findById(404L)).thenReturn(Optional.ofNullable(null));

        // then a 404 not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> boardService.getBoardById(404L))
                .withMessageContaining("Board not found");
    }
}
