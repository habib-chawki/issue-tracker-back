package com.habibInc.issueTracker.column;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnService {

    private ColumnRepository columnRepository;

    @Autowired
    public ColumnService(ColumnRepository columnRepository) {
        this.columnRepository = columnRepository;
    }

    public Column createColumn(Column column, Long boardId) {
        return columnRepository.save(column);
    }
}
