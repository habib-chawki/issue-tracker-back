package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.column.ColumnRepository;
import com.habibInc.issueTracker.column.ColumnService;
import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.sprint.Sprint;
import com.habibInc.issueTracker.sprint.SprintService;
import com.habibInc.issueTracker.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class BoardServiceTest {
    @InjectMocks
    @Spy
    BoardService boardService;

    @Mock
    BoardRepository boardRepository;

    @Mock
    SprintService sprintService;

    @Mock
    ColumnRepository columnRepository;

    Board board;
    User owner;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        board = new Board();

        board.setId(1L);
        board.setName("Kanban");

        // set board owner
        owner = User.builder().id(100L).email("board@owner.me").password("0Wn3R").build();
    }

    @Test
    public void itShouldCreateBoard() {
        Long sprintId = 10L;

        // given
        when(sprintService.getSprintById(sprintId)).thenReturn(null);
        when(boardRepository.save(board)).thenReturn(board);
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        doNothing().when(boardService).createToDoColumn(any(), any());
        doNothing().when(boardService).createBoardColumns(any());

        // when "createBoard()" service method is invoked
        Board createdBoard = boardService.createBoard(sprintId, board, owner);

        // then the response should be the successfully created board
        assertThat(createdBoard).isEqualTo(board);
    }

    @Test
    public void givenCreateBoard_itShouldSetBoardOwner() {
        Long sprintId = 10L;

        // given
        when(boardRepository.save(board)).thenReturn(board);
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        doNothing().when(boardService).createToDoColumn(any(), any());
        doNothing().when(boardService).createBoardColumns(any());


        // when "createBoard()" is invoked
        boardService.createBoard(sprintId, board, owner);

        // then the owner should be set
        assertThat(board.getOwner()).isEqualTo(owner);
    }

    @Test
    public void givenCreateBoard_itShouldSetSprint() {
        // given a sprint
        Sprint sprint = new Sprint();
        sprint.setId(10L);
        sprint.setName("Sprint");

        // given
        when(boardRepository.save(board)).thenReturn(board);
        when(sprintService.getSprintById(sprint.getId())).thenReturn(sprint);
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        doNothing().when(boardService).createToDoColumn(any(), any());
        doNothing().when(boardService).createBoardColumns(any());

        // when createBoard() is invoked
        boardService.createBoard(sprint.getId(), board, owner);

        // then the sprint should be set
        assertThat(board.getSprint()).isEqualTo(sprint);
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

    @Test
    public void itShouldDeleteBoardById() {
        // given the board owner
        board.setOwner(owner);

        // given the board exists
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        // when delete board by id service method is invoked
        boardService.deleteBoardById(board.getId(), owner);

        // then expect the board repository to have been invoked
        verify(boardRepository, times(1)).deleteById(board.getId());
    }

    @Test
    public void givenDeleteBoardById_whenBoardDoesNotExist_itShouldReturnBoardNotFoundError() {
        // when attempting to delete a board that does not exists
        // then a board not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> boardService.deleteBoardById(404L, owner))
                .withMessageContaining("Board not found");
    }

    @Test
    public void givenDeleteBoardById_whenAuthenticatedUserIsNotTheBoardOwner_itShouldReturnForbiddenOperationError() {
        // given the board owner
        board.setOwner(owner);

        // given the board exists
        when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));

        // when the authenticated user is not the board owner
        User notBoardOwner = User.builder().id(666L).email("not@owner.board").build();

        // then expect a 403 forbidden operation error
        assertThatExceptionOfType(ForbiddenOperationException.class)
                .isThrownBy(() -> boardService.deleteBoardById(board.getId(), notBoardOwner))
                .withMessageContaining("Forbidden");
    }
}
