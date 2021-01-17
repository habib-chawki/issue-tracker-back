package com.habibInc.issueTracker.column;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ColumnController.class)
@WithMockUser
public class ColumnControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    Column column;

    @BeforeEach
    public void setup(){
        // set up a column
        column = new Column();
        column.setName("To do");
    }

    @Test
    public void itShouldCreateColumn() throws Exception {
        String url = String.format("/boards/%s/columns", 100L);

        // given the request body
        String requestBody = mapper.writeValueAsString(column);

        // when a post request is made to create a new column
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
    }
}
