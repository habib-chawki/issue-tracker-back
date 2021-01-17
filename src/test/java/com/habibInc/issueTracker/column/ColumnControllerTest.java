package com.habibInc.issueTracker.column;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ColumnController.class)
public class ColumnControllerTest {

    @Autowired
    MockMvc mockMvc;

    Column column;

    @BeforeEach
    public void setup(){
        // set up a column
        column = new Column();
        column.setName("To do");
    }

    @Test
    public void itShouldCreateColumn(){
    }
}
