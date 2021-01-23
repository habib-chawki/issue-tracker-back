package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.board.Board;
import com.habibInc.issueTracker.board.BoardRepository;
import com.habibInc.issueTracker.board.BoardService;
import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.security.JwtUtil;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import com.habibInc.issueTracker.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ColumnIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ColumnRepository columnRepository;

    @Autowired
    ColumnService columnService;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    JwtUtil jwtUtil;

    HttpHeaders httpHeaders;

    User authenticatedUser;
    Column column;
    Board board;

    @BeforeEach
    public void auth() {
        // save the authenticated user
        authenticatedUser = User.builder()
                .email("authorizedr@user.in")
                .userName("authorized")
                .password("auth_pass")
                .build();

        authenticatedUser = userService.createUser(authenticatedUser);

        // generate auth token
        String authToken = jwtUtil.generateToken(authenticatedUser.getEmail());

        // set up authorization header
        httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + authToken);
    }

    @BeforeEach
    public void setup(){
        // create and save a board
        board = new Board();
        board.setName("column_board");

        board = boardService.createBoard(board);

        // set up a column
        column = new Column();
        column.setTitle("To do");
    }

    @Test
    public void itShouldCreateColumn() {
        String url = String.format("/boards/%s/column", board.getId());

        // given the create column post request
        HttpEntity<Column> httpEntity = new HttpEntity<>(column, httpHeaders);

        // when the request is received
        ResponseEntity<Column> response = restTemplate.exchange(url,
                HttpMethod.POST,
                httpEntity,
                Column.class
        );

        // then the column should be created successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(column);

        // the id and board should be set
        assertThat(response.getBody().getBoard()).isEqualTo(board);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
    }

    @Test
    public void givenCreateColumn_whenBoardDoesNotExist_itShouldReturnBoardNotFoundError() {
        // given the board does not exist
        String url = String.format("/boards/%s/column", 404L);

        // given the create column POST request
        HttpEntity<Column> httpEntity = new HttpEntity<>(column, httpHeaders);

        // when the request is received
        ResponseEntity<ApiError> response = restTemplate.exchange(url,
                HttpMethod.POST,
                httpEntity,
                ApiError.class
        );

        // then a 404 board not found error should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Board not found");
    }

    @Test
    public void itShouldGetColumnById() {
        // given the column is created
        Column savedColumn = columnService.createColumn(column, board.getId());

        // given the url and request body
        String url = String.format("/boards/%s/columns/%s", board.getId(), column.getId());
        HttpEntity<Column> httpEntity = new HttpEntity<>(httpHeaders);

        // when a GET request is made to retrieve the column by id
        ResponseEntity<Column> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, Column.class);

        // then expect the column to have been retrieved successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(savedColumn);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
    }

    @Test
    public void givenGetColumnById_whenBoardIdIsIncorrect_itShouldReturnBoardNotFoundError() {
        // given the column is created
        columnService.createColumn(column, board.getId());

        // given an incorrect board id
        String url = String.format("/boards/%s/columns/%s", 404L, column.getId());
        HttpEntity<Column> httpEntity = new HttpEntity<>(httpHeaders);

        // when a GET request is made to retrieve the column by id, with an incorrect board id
        ResponseEntity<ApiError> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, ApiError.class);

        // then expect a 404 board not found error to be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).containsIgnoringCase("Board not found");
    }

    @Test
    public void itShouldGetPaginatedListOfIssues() {
        // given a created column
        Column createdColumn = columnService.createColumn(column, board.getId());

        // given a list of issues
        List<Issue> issues = List.of(
                Issue.builder().summary("issue 1").column(createdColumn).build(),
                Issue.builder().summary("issue 2").column(createdColumn).build(),
                Issue.builder().summary("issue 3").column(createdColumn).build(),
                Issue.builder().summary("issue 4").column(createdColumn).build(),
                Issue.builder().summary("issue 5").column(createdColumn).build()
        );

        // save the list of issues
        issues = (List<Issue>) issueRepository.saveAll(issues);

        // given a GET request to fetch a paginated list of issues
        int page = 0;
        int size = 3;

        HttpEntity<List<Issue>> httpEntity = new HttpEntity<>(httpHeaders);
        String url = String.format(
                "/boards/%s/columns/%s/issues?page=%s&size=%s",
                board.getId(), createdColumn.getId(), page, size
        );

        // when the request is made
        ResponseEntity<Issue[]> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, Issue[].class);

        // then expect to get a paginated list of issues
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsAll(issues.subList(0, size));
    }

    @Test
    public void givenGetPaginatedListOfIssues_itShouldNotReturnIssuesBelongingToOtherColumns() {
        // given two distinct columns
        Column targetedColumn, anotherColumn;

        targetedColumn = new Column();
        targetedColumn.setTitle("Target");
        targetedColumn.setBoard(board);

        anotherColumn = new Column();
        anotherColumn.setTitle("Other");
        anotherColumn.setBoard(board);

        columnRepository.saveAll(List.of(targetedColumn, anotherColumn));

        // given a list of issues belonging to the targeted column
        List<Issue> targetedColumnIssues = List.of(
                Issue.builder().summary("issue 1").column(targetedColumn).build(),
                Issue.builder().summary("issue 4").column(targetedColumn).build()
        );

        // given a list of issues belonging to another column
        List<Issue> anotherColumnIssues = List.of(
                Issue.builder().summary("issue 2").column(anotherColumn).build(),
                Issue.builder().summary("issue 3").column(anotherColumn).build(),
                Issue.builder().summary("issue 5").column(anotherColumn).build()
        );

        // save the list of issues
        targetedColumnIssues = (List<Issue>) issueRepository.saveAll(targetedColumnIssues);
        anotherColumnIssues = (List<Issue>) issueRepository.saveAll(anotherColumnIssues);

        // given a GET request to fetch a paginated list of issues of the targeted column
        int page = 0;
        int size = 3;

        HttpEntity<List<Issue>> httpEntity = new HttpEntity<>(httpHeaders);
        String url = String.format(
                "/boards/%s/columns/%s/issues?page=%s&size=%s",
                board.getId(), targetedColumn.getId(), page, size
        );

        // when the request is made
        ResponseEntity<Issue[]> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, Issue[].class);

        // then expect the retrieved issues to belong to the targeted column only
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isEqualTo(targetedColumnIssues.size());
        assertThat(response.getBody()).containsAll(targetedColumnIssues);
        assertThat(response.getBody()).doesNotContainAnyElementsOf(anotherColumnIssues);
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
        issueRepository.deleteAll();
        boardRepository.deleteAll();
    }
}
