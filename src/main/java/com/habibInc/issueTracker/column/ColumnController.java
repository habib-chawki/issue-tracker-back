package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.issue.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}")

public class ColumnController {

    private ColumnService columnService;

    @Autowired
    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }

    @PostMapping("/column")
    @ResponseStatus(HttpStatus.CREATED)
    public Column createColumn(@PathVariable("boardId") String id,
                               @RequestBody Column column
                               ){
        try{
            Long boardId = Long.parseLong(id);

            return columnService.createColumn(boardId, column);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }
    }

    @PostMapping("/columns")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Column> createColumns(@PathVariable("boardId") String id,
                                      @RequestBody List<Column> columns){
        try{
            Long boardId = Long.parseLong(id);

            return columnService.createColumns(boardId, columns);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }
    }

    @GetMapping("/columns/{columnId}")
    @ResponseStatus(HttpStatus.OK)
    public Column getColumnById(@PathVariable String boardId, @PathVariable String columnId) {
        try {
            Long parsedBoardId = Long.parseLong(boardId);
            Long parsedColumnId = Long.parseLong(columnId);

            return columnService.getColumnById(parsedBoardId, parsedColumnId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }

    @GetMapping(path = "/columns/{columnId}/issues")
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

    @DeleteMapping(path = "/columns/{columnId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteColumnById(@PathVariable String boardId,
                                 @PathVariable String columnId) {
        try{
            Long parsedBoardId = Long.parseLong(boardId);
            Long parsedColumnId = Long.parseLong(columnId);

            columnService.deleteColumnById(parsedBoardId, parsedColumnId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }

    @PatchMapping(path = "/columns/{columnId}")
    @ResponseStatus(HttpStatus.OK)
    public Column updateColumnTitle(@RequestBody String title,
                                    @PathVariable String boardId,
                                    @PathVariable String columnId){
        try{
            Long parsedBoardId = Long.parseLong(boardId);
            Long parsedColumnId = Long.parseLong(columnId);

            return columnService.updateTitle(parsedBoardId, parsedColumnId, title);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }
}
