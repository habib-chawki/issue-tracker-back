package com.habibInc.issueTracker.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@WithMockUser
public class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BoardService boardService;

    Board board;

    @BeforeEach
    public void setup() {
        board = new Board();

        board.setId(1L);
        board.setName("Scrum");
    }

    @Test
    public void itShouldCreateBoard() throws Exception {
        // given the board service
        when(boardService.createBoard(any(Board.class))).thenReturn(board);

        // when a request to create a board is made
        String url = "/boards";
        String requestBody = mapper.writeValueAsString(board);

        // then the response should be the created board
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody));
    }

    @Test
    public void itShouldGetBoardById() throws Exception {
        // given the board service
        when(boardService.getBoardById(board.getId())).thenReturn(board);

        // given the get board by id endpoint
        String url = "/boards/" + board.getId();

        // expect the response to be the retrieved board by id
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
