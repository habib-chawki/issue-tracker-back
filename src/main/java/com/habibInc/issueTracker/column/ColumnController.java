package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/columns")

public class ColumnController {

    private ColumnService columnService;

    @Autowired
    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Column createColumn(@RequestBody Column column, @PathVariable("boardId") String id){
        try{
            Long boardId = Long.parseLong(id);
            return columnService.createColumn(column, boardId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }
    }

    @GetMapping(path = "{columnId}/issues")
    @ResponseStatus(HttpStatus.OK)
    public List<Issue> getPaginatedListOfIssues(@PathVariable String columnId,
                                                @PathVariable String boardId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        try{
            Long parsedColumnId = Long.parseLong(columnId);
            Long parsedBoardId = Long.parseLong(boardId);

            return columnService.getPaginatedListOfIssues(parsedBoardId, parsedColumnId, page, size);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }
}
