package com.habibInc.issueTracker.column;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class ColumnServiceTest {
    @InjectMocks
    ColumnService columnService;

    @Mock
    ColumnRepository columnRepository;

    Column column;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        column = new Column();
        column.setId(1L);
        column.setName("In progress");
    }

    @Test
    public void itShouldCreateColumn() {
        when(columnRepository.save(column)).thenReturn(column);

        Column response = columnService.createColumn(column, 100L);

        Assertions.assertThat(response).isEqualTo(column);
    }
}
