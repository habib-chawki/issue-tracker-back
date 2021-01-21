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

    public Column createColumn(Column column, Long boardId) {
        // invoke board service to fetch the board by id (throws an exception)
        Board board = boardService.getBoardById(boardId);

        // set the board
        column.setBoard(board);

        // save the column
        return columnRepository.save(column);
    }

    public Column getColumnById(Long boardId, Long columnId) {
        return columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));
    }

    public List<Issue> getPaginatedListOfIssues(Long boardId, Long columnId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return issueRepository.findByColumnId(columnId, pageable);
    }


}
