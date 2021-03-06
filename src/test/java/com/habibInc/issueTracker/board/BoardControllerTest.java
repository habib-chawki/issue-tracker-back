package com.habibInc.issueTracker.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@WithMockUser
public class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BoardService boardService;

    @SpyBean
    ModelMapper modelMapper;

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
        when(boardService.createBoard(any(), eq(board), any())).thenReturn(board);

        // given the expected response
        String expectedResponse = objectMapper.writeValueAsString(
                modelMapper.map(board, BoardDto.class)
        );

        // when a POST request to create a board is made
        String url = "/boards";
        String requestBody = objectMapper.writeValueAsString(board);

        // then the response should be the created board
        mockMvc.perform(post(url)
                .queryParam("sprint", String.valueOf(10L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponse));
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

    @Test
    public void givenGetBoardById_whenBoardIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        // given an invalid board id
        String url = "/boards/invalid_id";

        // expect an invalid board id error
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid board id"));
    }

    @Test
    public void itShouldDeleteBoard() throws Exception {
        doNothing().when(boardService).deleteBoardById(eq(board.getId()), any(User.class));

        // when a DELETE request is made then expect the board to have been deleted successfully
        mockMvc.perform(delete("/boards/" + board.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void givenDeleteBoard_whenBoardIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        // when the board id is invalid then expect an invalid board id error
        mockMvc.perform(delete("/boards/invalid_id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Invalid board id"));
    }
}
