package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.column.Column;
import com.habibInc.issueTracker.column.ColumnRepository;
import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.sprint.Sprint;
import com.habibInc.issueTracker.sprint.SprintService;
import com.habibInc.issueTracker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final IssueRepository issueRepository;
    private final SprintService sprintService;

    @Autowired
    public BoardService(BoardRepository boardRepository, ColumnRepository columnRepository, IssueRepository issueRepository, SprintService sprintService) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.issueRepository = issueRepository;
        this.sprintService = sprintService;
    }

    public Board createBoard(Long sprintId, Board board, User authenticatedUser){
        // fetch the sprint (throws sprint not found error)
        Sprint sprint = sprintService.getSprintById(sprintId);

        // set board owner and sprint
        board.setSprint(sprint);
        board.setOwner(authenticatedUser);

        // save the board
        boardRepository.save(board);

        // create the to do column
        createToDoColumn(sprint, board);

        // create other board columns
        createBoardColumns(board);

        // return the board after having saved its columns
        return getBoardById(board.getId());
    }

    public void createToDoColumn(Sprint sprint, Board board){
        // create and save the to do column
        Column toDoColumn = new Column();
        toDoColumn.setTitle("To Do");
        toDoColumn.setBoard(board);

        toDoColumn = columnRepository.save(toDoColumn);

        // set the sprint backlog as the to do column issues
        setToDoColumnIssues(toDoColumn, sprint.getBacklog());
    }

    public void setToDoColumnIssues(Column column, List<Issue> issues) {
        // extract the id of each sprint backlog issue
        List<Long> issueIds =
                issues.stream().map((issue) -> issue.getId()).collect(Collectors.toList());

        // update the column
        issueRepository.updateIssuesColumn(column.getId(), issueIds);
    }

    public void createBoardColumns(Board board) {
        columnRepository.saveAll(List.of(
                Column.builder().title("In progress").board(board).build(),
                Column.builder().title("Done").board(board).build()
        ));
    }

    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));
    }

    public void deleteBoardById(Long boardId, User authenticatedUser) {
        // find board by id (throws board not found exception)
        Board board = this.getBoardById(boardId);

        // check whether the authenticated user is the board owner or not
        if(!board.getOwner().equals(authenticatedUser)){
            throw new ForbiddenOperationException("Forbidden operation");
        }

        boardRepository.deleteById(boardId);
    }
}
