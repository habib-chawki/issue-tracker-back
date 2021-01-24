package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.board.Board;
import com.habibInc.issueTracker.board.BoardService;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnService {

    private ColumnRepository columnRepository;
    private IssueRepository issueRepository;
    private BoardService boardService;

    @Autowired
    public ColumnService(ColumnRepository columnRepository, IssueRepository issueRepository, BoardService boardService) {
        this.columnRepository = columnRepository;
        this.issueRepository = issueRepository;
        this.boardService = boardService;
    }

    public Column createColumn(Long boardId, Column column) {
        // invoke board service to fetch the board by id (throws an exception)
        Board board = boardService.getBoardById(boardId);

        // set the board
        column.setBoard(board);

        // save the column
        return columnRepository.save(column);
    }

    public List<Column> createColumns(Long boardId, List<Column> columns) {
        return (List<Column>) columnRepository.saveAll(columns);
    }

    public Column getColumnById(Long boardId, Long columnId) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));

        // check if the column belongs to the board specified by id
        if (column.getBoard().getId() != boardId) {
            throw new ResourceNotFoundException("Board not found");
        } else {
            return column;
        }
    }

    public List<Issue> getPaginatedListOfIssues(Long boardId, Long columnId, int page, int size) {
        // fetch the column by id (throws either column or board not found exception)
        getColumnById(boardId, columnId);

        // when both the column and board exist, fetch the paginated list of issues
        Pageable pageable = PageRequest.of(page, size);
        return issueRepository.findByColumnId(columnId, pageable);
    }
}
