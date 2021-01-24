package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.column.ColumnRepository;
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
public class BoardIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ColumnRepository columnRepository;

    User authenticatedUser;
    HttpHeaders httpHeaders;

    Board board;

    @BeforeEach
    public void auth() {
        // set up the authenticated user
        authenticatedUser = User.builder()
                .userName("authenticated_user")
                .email("authenticated@user.in")
                .password("auth_pass")
                .build();

        // save the user to pass the authorization filter
        userService.createUser(authenticatedUser);

        // generate an auth token for the authenticated user
        String token = jwtUtil.generateToken(authenticatedUser.getEmail());

        // set up the authorization header
        httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + token);
    }

    @BeforeEach
    public void setup() {
        board = new Board();
        board.setName("ScrumOrKanban");
    }

    @Test
    public void itShouldCreateBoard() {
        // given the url endpoint and the request
        String url = "/boards";
        HttpEntity<Board> httpEntity = new HttpEntity<>(board, httpHeaders);

        // when a post request is made to create a new board
        ResponseEntity<Board> response =
                restTemplate.exchange(url, HttpMethod.POST, httpEntity, Board.class);

        // then the board should be created successfully with an autogenerated id
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response).isEqualToComparingOnlyGivenFields(board);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
    }

    @Test
    public void itShouldGetBoardById() {
        // given a board is created
        Board createdBoard = boardService.createBoard(board, authenticatedUser);

        // given the request
        String url = "/boards/" + createdBoard.getId();
        HttpEntity<Board> httpEntity = new HttpEntity<>(httpHeaders);

        // when a get request is made to fetch the board by id
        ResponseEntity<Board> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, Board.class);

        // then the board should be retrieved successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
        assertThat(response.getBody()).isEqualTo(createdBoard);
    }

    @Test
    public void givenGetBoardById_itShouldGetListOfColumns() {
        // given a board
        Board createdBoard = boardService.createBoard(board, authenticatedUser);

        // given a list of columns
        List<Column> columns = List.of(
                Column.builder().title("column 1").board(board).build(),
                Column.builder().title("column 2").board(board).build(),
                Column.builder().title("column 3").board(board).build(),
                Column.builder().title("column 4").board(board).build()
        );

        columns = (List<Column>) columnRepository.saveAll(columns);

        // given the request
        String url = "/boards/" + createdBoard.getId();
        HttpEntity<Board> httpEntity = new HttpEntity<>(httpHeaders);

        // when a GET request is made to fetch the board by id
        ResponseEntity<Board> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, Board.class);

        // then the board should be retrieved along with a list of columns
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getColumns()).isEqualTo(columns);
    }

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
    }
}
