package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.issue.Issue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        column.setTitle("In progress");
    }

    @Test
    public void itShouldCreateColumn() {
        when(columnRepository.save(column)).thenReturn(column);

        Column response = columnService.createColumn(column, 100L);

        assertThat(response).isEqualTo(column);
    }

    @Test
    public void itShouldGetPaginatedListOfIssues() {
        // given a list of issues
        List<Issue> issues = new ArrayList<>(List.of(
                Issue.builder().id(1L).build(),
                Issue.builder().id(2L).build(),
                Issue.builder().id(3L).build(),
                Issue.builder().id(4L).build())
        );

        int page = 0;
        int size = 4;

        // given the pageable object
        Pageable pageable = PageRequest.of(page, size);

        when(columnRepository.findAllIssues(pageable)).thenReturn(issues);

        List<Issue> response = columnService.getPaginatedListOfIssues(page, size);

        assertThat(response).isEqualTo(issues);
    }
}
