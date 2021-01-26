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

import static org.assertj.core.api.Assertions.*;
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

        // set the board
        column.setBoard(board);
    }

    @Test
    public void itShouldCreateColumn() {
        when(boardService.getBoardById(board.getId())).thenReturn(board);
        when(columnRepository.save(column)).thenReturn(column);

        // when the column is created
        Column response = columnService.createColumn(board.getId(), column);

        // then expect the response to be the created column with the board property set
        assertThat(response.getBoard()).isEqualTo(board);
        assertThat(response).isEqualTo(column);
    }

    @Test
    public void itShouldGetColumnById() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        Column retrievedColumn = columnService.getColumnById(board.getId(), column.getId());

        assertThat(retrievedColumn).isEqualTo(column);
    }

    @Test
    public void givenGetColumnById_whenColumnDoesNotExist_itShouldReturnColumnNotFoundError() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.ofNullable(null));

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.getColumnById(board.getId(), 404L))
                .withMessageContaining("Column not found");
    }

    @Test
    public void givenGetColumnById_whenBoardIdIsIncorrect_itShouldReturnBoardNotFoundError () {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        // when the board id does not exist, then expect a board not found error to be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.getColumnById(404L, column.getId()))
                .withMessageContaining("Board not found");
    }

    @Test
    public void itShouldGetPaginatedListOfIssues() {
        // given the column exists
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        // given a list of issues
        List<Issue> issues = new ArrayList<>(List.of(
                Issue.builder().id(1L).build(),
                Issue.builder().id(2L).build(),
                Issue.builder().id(3L).build(),
                Issue.builder().id(4L).build())
        );

        // given the pageable object
        int page = 0;
        int size = 4;
        Pageable pageable = PageRequest.of(page, size);

        // given the issue repository returns a list of issues
        when(issueRepository.findByColumnId(eq(column.getId()), eq(pageable))).thenReturn(issues);

        // when the column service is invoked to get a paginated list of issues
        List<Issue> response = columnService.getPaginatedListOfIssues(board.getId(), column.getId(), page, size);

        // then the response should be the paginated list of issues
        assertThat(response).isEqualTo(issues);
    }

    @Test
    public void givenGetPaginatedListOfIssues_whenColumnDoesNotExist_itShouldReturnColumnNotFoundError() {
       // when the column is not found
        when(columnRepository.findById(column.getId())).thenReturn(Optional.ofNullable(null));

        // then a 404 column not found error should be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.getPaginatedListOfIssues(column.getBoard().getId(), column.getId(), 0, 3))
                .withMessageContaining("Column not found");
    }

    @Test
    public void givenGetPaginatedListOfIssues_whenBoardDoesNotExist_itShouldReturnBoardNotFoundError() {
        // given the column is found by id
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        // when the board id is incorrect, then a 404 board not found error should returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.getPaginatedListOfIssues(404L, column.getId(), 0, 3))
                .withMessageContaining("Board not found");

    }

    @Test
    public void itShouldCreateColumnsList() {
        // given a list of columns
        List<Column> columns = List.of(
                Column.builder().id(1L).title("column 1").build(),
                Column.builder().id(2L).title("column 2").build(),
                Column.builder().id(3L).title("column 3").build(),
                Column.builder().id(4L).title("column 4").build()
        );

        // given the board is found by id
        when(boardService.getBoardById(board.getId())).thenReturn(board);

        // given the column repository returns the list of saved columns
        when(columnRepository.saveAll(columns)).thenReturn(columns);

        // when createColumns() is invoked
        List<Column> response = columnService.createColumns(board.getId(), columns);

        // then the response should be the list of saved columns
        assertThat(response).isEqualTo(columns);
    }

    @Test
    public void givenCreateColumns_whenBoardDoesNotExist_itShouldReturnBoardNotFoundError() {
        // given a list of columns
        List<Column> columns = List.of(
                Column.builder().id(1L).title("column 1").build(),
                Column.builder().id(2L).title("column 2").build(),
                Column.builder().id(3L).title("column 3").build(),
                Column.builder().id(4L).title("column 4").build()
        );

        when(boardService.getBoardById(404L))
                .thenThrow(new ResourceNotFoundException("Board not found"));

        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.createColumns(404L, columns))
                .withMessageContaining("Board not found");
    }

    @Test
    public void givenCreateColumns_itShouldSetBoardForEachColumn() {
        // given a list of columns
        List<Column> columns = List.of(
                Column.builder().id(1L).title("column 1").build(),
                Column.builder().id(2L).title("column 2").build(),
                Column.builder().id(3L).title("column 3").build()
        );

        // given the board is found by id
        when(boardService.getBoardById(board.getId())).thenReturn(board);

        // when createColumns() is invoked
        columnService.createColumns(board.getId(), columns);

        // then expect the board to have been set for each column
        columns.stream()
                .forEach((column) -> assertThat(column.getBoard()).isEqualTo(board));
    }

    @Test
    public void itShouldDeleteColumnById() {

    }

    @Test
    public void givenDeleteColumnById_whenColumnDoesNotExist_itShouldReturnColumnNotFoundError() {
        // given the column does not exist
        when(columnRepository.findById(column.getId())).thenReturn(Optional.ofNullable(null));

        // expect a column not found error to be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.deleteColumnById(column.getBoard().getId(), column.getId()))
                .withMessageContaining("Column not found");
    }

    @Test
    public void givenDeleteColumnById_whenBoardDoesNotExist_itShouldReturnBoardNotFoundError() {

    }
}
