package com.habibInc.issueTracker.column;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ColumnRepositoryTest {
    @Autowired
    ColumnRepository columnRepository;

    Column column;

    @BeforeEach
    public void setup() {
        column = new Column();
        column.setTitle("Done");
    }

    @Test
    public void itShouldSaveColumn() {
        // when invoking the column repository to save a new column
        Column response = columnRepository.save(column);

        // then the column should be saved successfully with an autogenerated id
        assertThat(response).isEqualToComparingOnlyGivenFields(column);
        assertThat(response.getId()).isNotNull().isPositive();
    }

    @Test
    public void itShouldFindColumnById() {
        // given a created column
        Column createdColumn = columnRepository.save(column);

        // when findById() is invoked
        Optional<Column> response = columnRepository.findById(createdColumn.getId());

        // then the column should be retrieved successfully
        assertThat(response.get()).isEqualTo(createdColumn);
    }

    @Test
    public void itShouldSaveAllColumns() {
        // given a list of columns
        List<Column> columns = List.of(
                Column.builder().title("column 1").build(),
                Column.builder().title("column 2").build(),
                Column.builder().title("column 3").build()
        );

        // when saveAll() is invoked
        List<Column> response = (List<Column>) columnRepository.saveAll(columns);

        // then the columns should be saved successfully
        assertThat(response).isEqualTo(columns);

        // expect each column to have been saved with an autogenerated id
        response.stream().forEach(
                (column) -> assertThat(column.getId()).isNotNull().isPositive()
        );
    }
}
