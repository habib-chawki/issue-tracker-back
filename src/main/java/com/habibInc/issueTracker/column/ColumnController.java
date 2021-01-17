package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards/{boardId}/columns")

public class ColumnController {

    private ColumnService columnService;

    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)

    public Column createColumn(@RequestBody Column column, @PathVariable String boardId){
        try{
            Long parsedBoardId = Long.parseLong(boardId);
            return columnService.createColumn(column, parsedBoardId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }
    }
}
