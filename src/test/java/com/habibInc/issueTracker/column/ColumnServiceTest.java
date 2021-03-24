package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.board.Board;
import com.habibInc.issueTracker.board.BoardService;
import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.issue.IssueService;
import com.habibInc.issueTracker.user.User;
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

    @Mock
    IssueService issueService;

    Column column;
    Board board;
    User boardOwner;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        // set a board owner
        boardOwner = new User();
        boardOwner.setId(200L);
        boardOwner.setEmail("board@owner.me");
        boardOwner.setPassword("mYb0@Rd");

        // set up a board
        board = new Board();
        board.setId(100L);
        board.setName("column_board");
        board.setOwner(boardOwner);

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
        // given the column exists
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        // when deleteColumnById() is called
        columnService.deleteColumnById(column.getBoard().getId(), column.getId(), boardOwner);

        // then expect column repository to have been invoked
        verify(columnRepository, times(1)).deleteById(column.getId());
    }

    @Test
    public void givenDeleteColumnById_whenAuthenticatedUserIsNotTheBoardOwner_itShouldReturnForbiddenOperationError() {
        // given a random user
        User notOwner = User.builder().id(666L).email("not@column.owner").password("!owner").build();

        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        doNothing().when(columnRepository).deleteById(column.getId());

        // when deleteColumnById() is called and the authenticated user is not the board owner
        // then expect a forbidden operation error
        assertThatExceptionOfType(ForbiddenOperationException.class)
                .isThrownBy(
                        () -> columnService.deleteColumnById(column.getBoard().getId(), column.getId(), notOwner)
                ).withMessageContaining("Forbidden operation");
    }

    @Test
    public void givenDeleteColumnById_whenColumnDoesNotExist_itShouldReturnColumnNotFoundError() {
        // given the column does not exist
        when(columnRepository.findById(column.getId())).thenReturn(Optional.ofNullable(null));

        // expect a column not found error to be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.deleteColumnById(column.getBoard().getId(), column.getId(), boardOwner))
                .withMessageContaining("Column not found");
    }

    @Test
    public void givenDeleteColumnById_whenBoardDoesNotExist_itShouldReturnBoardNotFoundError() {
        // given the column exists
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        // given the board does not exist
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.deleteColumnById(404L, column.getId(), boardOwner))
                .withMessageContaining("Board not found");
    }

    @Test
    public void itShouldUpdateColumnTitle() {
        // given the new column title
        String newTitle = "update column title";

        // given the column repository response
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(columnRepository.save(column)).thenReturn(column);

        // when updateTitle() is called
        String response =
                columnService.updateTitle(this.column.getBoard().getId(), this.column.getId(), newTitle, boardOwner);

        // then the response should be the column with the updated title
        assertThat(response).isEqualTo(newTitle);
    }

    @Test
    public void givenUpdateColumnTitle_whenAuthenticatedUserIsNotTheBoardOwner_itShouldReturnForbiddenOperationError() {
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));
        when(columnRepository.save(column)).thenReturn(column);

        // given a random user
        User notOwner = User.builder().id(666L).email("not@column.owner").password("!owner").build();

        // when updateTitle() is called and the authenticated user is not the board owner
        // then expect a forbidden operation error
        assertThatExceptionOfType(ForbiddenOperationException.class)
                .isThrownBy(
                        () -> columnService.updateTitle(column.getBoard().getId(), column.getId(), "Updated title", notOwner)
                ).withMessageContaining("Forbidden operation");
    }

    @Test
    public void givenUpdateColumnTitle_whenColumnDoesNotExist_itShouldReturnColumnNotFoundError() {
        // given the column does not exist
        when(columnRepository.findById(column.getId())).thenReturn(Optional.ofNullable(null));

        // expect a column not found error to be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.updateTitle(
                                column.getBoard().getId(), column.getId(), "new title", boardOwner))
                .withMessageContaining("Column not found");
    }

    @Test
    public void givenUpdateColumnTitle_whenBoardDoesNotExists_itShouldReturnBoardNotFoundError() {
        // given the column does not exist
        when(columnRepository.findById(column.getId())).thenReturn(Optional.of(column));

        // expect a column not found error to be returned
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> columnService.updateTitle(
                        404L, column.getId(), "new title", boardOwner))
                .withMessageContaining("Board not found");
    }

    @Test
    public void itShouldUpdateIssueColumn() {
        // given a column
        Column newColumn = new Column();
        newColumn.setId(555L);
        newColumn.setTitle("Column 2");
        newColumn.setBoard(board);

        // given an issue
        Issue issue = Issue.builder().id(100L).column(column).summary("issue 1").build();

        when(columnRepository.findById(newColumn.getId())).thenReturn(Optional.of(newColumn));
        when(issueService.getIssueById(issue.getId())).thenReturn(issue);
        when(issueRepository.save(issue)).thenReturn(issue);

        // when the column service is invoked to update the issue column
        columnService.updateIssueColumn(board.getId(), column.getId(), issue.getId(), newColumn.getId());

        // then expect the column to have been updated successfully
        assertThat(issue.getColumn()).isEqualTo(newColumn);
    }
}
