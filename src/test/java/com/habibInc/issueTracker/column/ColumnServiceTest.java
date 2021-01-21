package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.board.Board;
import com.habibInc.issueTracker.board.BoardService;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class ColumnServiceTest {
    @InjectMocks
    ColumnService columnService;

    @Mock
    ColumnRepository columnRepository;

    @Mock
    IssueRepository issueRepository;

    @Mock
    BoardService boardService;

    Column column;
    Board board;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        // set up a board
        board = new Board();
        board.setId(100L);
        board.setName("column_board");

        // set up a column
        column = new Column();
        column.setId(1L);
        column.setTitle("In progress");
    }

    @Test
    public void itShouldCreateColumn() {
        when(boardService.getBoardById(board.getId())).thenReturn(board);
        when(columnRepository.save(column)).thenReturn(column);

        // when the column is created
        Column response = columnService.createColumn(column, 100L);

        // then expect the response to be the created column with the board property set
        assertThat(response.getBoard()).isEqualTo(board);
        assertThat(response).isEqualTo(column);
    }

    @Test
    public void itShouldGetColumnById() {
        // given the column board
        column.setBoard(board);

        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        Column retrievedColumn = columnService.getColumnById(board.getId(), column.getId());

        assertThat(retrievedColumn).isEqualTo(column);
    }

    @Test
    public void givenGetColumnById_whenColumnDoesNotExist_itShouldReturnColumnNotFoundError() {
        // given the column board
        column.setBoard(board);
        when(columnRepository.findById(column.getId())).thenReturn(Optional.ofNullable(null));

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.getColumnById(board.getId(), 404L))
                .withMessageContaining("Column not found");
    }

    @Test
    public void givenGetColumnById_whenBoardIdIsIncorrect_itShouldReturnBoardNotFoundError () {
        // given the column board
        column.setBoard(board);
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        // when the board id does not exist, then expect a board not found error to be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.getColumnById(404L, column.getId()))
                .withMessageContaining("Board not found");
    }

    @Test
    public void itShouldGetPaginatedListOfIssues() {
        // given the board id
        Long boardId = 100L;

        // given a list of issues
        List<Issue> issues = new ArrayList<>(List.of(
                Issue.builder().id(1L).build(),
                Issue.builder().id(2L).build(),
                Issue.builder().id(3L).build(),
                Issue.builder().id(4L).build())
        );

        int page = 0;
        int size = 4;

        // given the pageable object
        Pageable pageable = PageRequest.of(page, size);

        // given the issue repository returns a list of issues
        when(issueRepository.findByColumnId(eq(column.getId()), eq(pageable))).thenReturn(issues);

        // when the column service is invoked
        List<Issue> response = columnService.getPaginatedListOfIssues(boardId, column.getId(), page, size);

        // then the response should be the paginated list of issues
        assertThat(response).isEqualTo(issues);
    }

    @Test
    public void givenGetPaginatedListOfIssues_whenColumnDoesNotExist_itShouldReturnColumnNotFoundError() {
        // given the column board
        column.setBoard(board);

        // when the column is not found
        when(columnRepository.findById(column.getId())).thenReturn(Optional.ofNullable(null));

        // then a 404 column not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.getPaginatedListOfIssues(column.getBoard().getId(), column.getId(), 0, 3))
                .withMessageContaining("Column not found");
    }

    @Test
    public void givenGetPaginatedListOfIssues_whenBoardDoesNotExist_itShouldReturnBoardNotFoundError() {
        // given the column board
        column.setBoard(board);

        // given the column is found by id
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        // when the board id is incorrect, then a 404 board not found error should returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.getPaginatedListOfIssues(404L, column.getId(), 0, 3))
                .withMessageContaining("Board not found");

    }
}
