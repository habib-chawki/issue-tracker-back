package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Issue> getPaginatedListOfIssues(int page, int size) {
        return null;
    }
}
