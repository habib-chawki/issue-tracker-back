package com.habibInc.issueTracker.column;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void setup(){
        // set up a column
        column = new Column();
        column.setName("To do");
    }

    @Test
    public void itShouldCreateColumn() throws Exception {
        // when column service is invoked then return the created column
        when(columnService.createColumn(any(Column.class))).thenReturn(column);

        String url = String.format("/boards/%s/columns", 100L);

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
}
