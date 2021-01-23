package com.habibInc.issueTracker.column;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.issue.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ColumnController.class)
@WithMockUser
public class ColumnControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ColumnService columnService;

    Column column;

    @BeforeEach
    public void setup() {
        // set up a column
        column = new Column();
        column.setId(1L);
        column.setTitle("To do");
    }

    @Test
    public void itShouldCreateColumn() throws Exception {
        Long boardId = 100L;
        String url = String.format("/boards/%s/column", boardId);

        // when column service is invoked then return the created column
        when(columnService.createColumn(any(Column.class), eq(boardId))).thenReturn(column);

        // given the request body
        String requestBody = mapper.writeValueAsString(column);

        // when a post request is made then the column should to be created successfully
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody));
    }

    @Test
    public void givenCreateColumn_whenBoardIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        String url = String.format("/boards/%s/column", "invalid_id");

        // given the request body
        String requestBody = mapper.writeValueAsString(column);
        String errorMessage = "Invalid board id";

        // when the board id is invalid then a 400 bad request error should be returned
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void itShouldGetColumnById() throws Exception {
        Long boardId = 100L;
        String url = String.format("/boards/%s/columns/%s", boardId, column.getId());

        when(columnService.getColumnById(boardId, column.getId())).thenReturn(column);

        // expect the column to have been retrieved successfully
        mockMvc.perform(get(url)).andExpect(status().isOk());
    }

    @Test
    public void givenGetColumnById_whenIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        // given an invalid board id
        String url = String.format("/boards/%s/columns/%s", "invalid_board_id", column.getId());

        // given the error message
        String errorMessage = "Invalid id";

        // when the board id is invalid then a 400 bad request error should be returned
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));

        // given an invalid column id
        url = String.format("/boards/%s/columns/%s", 100L, "invalid_column_id");

        // when the column id is invalid then a 400 bad request error should be returned
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    public void itShouldGetPaginatedListOfIssues() throws Exception {
        Long boardId = 100L;

        Integer page = 0;
        Integer size = 4;

        // given the endpoint url
        String url = String.format(
                "/boards/%s/columns/%s/issues?page=%s&size=%s",
                boardId, column.getId(), page, size
        );

        // given a list of issues
        List<Issue> issues = new ArrayList<>(List.of(
                Issue.builder().id(1L).build(),
                Issue.builder().id(2L).build(),
                Issue.builder().id(3L).build(),
                Issue.builder().id(4L).build())
        );

        String response = mapper.writeValueAsString(issues);

        // given the column service returns a list of issues
        when(columnService.getPaginatedListOfIssues(eq(100L), eq(column.getId()),
                eq(page), eq(size))).thenReturn(issues);

        // expect the response to be the paginated list of issues
        mockMvc.perform(get(url)
                .param("page", page.toString())
                .param("size", size.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(response));
    }

    @Test
    public void givenGetPaginatedListOfIssues_whenIdIsInvalid_itShouldReturnInvalidIdError() throws Exception {
        // given an invalid column id
        String url = String.format("/boards/%s/columns/%s/issues", 100L, "invalid_column_id");

        // when a request is made with the invalid column id
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid id"));

        // given an invalid board id
        url = String.format("/boards/%s/columns/%s/issues", "invalid_board_id", column.getId());

        // when a request is made with the invalid board id
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Invalid id"));
    }

    @Test
    public void itShouldCreateListOfColumns() throws Exception {
        Long boardId = 100L;
        String url = String.format("/boards/%s/columns", boardId);

        // given a list of columns
        List<Column> columnsList = List.of(
                Column.builder().id(1L).title("Column 1").build(),
                Column.builder().id(2L).title("Column 2").build(),
                Column.builder().id(3L).title("Column 3").build()
        );

        // given the request body
        String requestBody = mapper.writeValueAsString(columnsList);

        // when a POST request is made to create the list of columns
        // then the response should be a 201 CREATED
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody));
    }
}
