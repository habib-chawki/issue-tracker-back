package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.board.Board;
import com.habibInc.issueTracker.board.BoardRepository;
import com.habibInc.issueTracker.board.BoardService;
import com.habibInc.issueTracker.exceptionhandler.ApiError;
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
        String url = String.format("/boards/%s/columns", board.getId());

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
    public void givenCreateColumn_whenBoardDoesNotExist_itShouldReturnNotFoundError() {
        // given the board does not exist
        String url = String.format("/boards/%s/columns", 404L);

        // given the create column post request
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
        // given a column is saved
        Column savedColumn = columnRepository.save(column);

        // given the url and request body
        String url = String.format("/boards/%s/columns/%s", 100L, column.getId());
        HttpEntity<Column> httpEntity = new HttpEntity<>(httpHeaders);

        // when a GET request is made to retrieve the column by id
        ResponseEntity<Column> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, Column.class);

        // then expect the column to have been retrieved successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(savedColumn);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }
}
