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

    public Column createColumn(@RequestBody Column column, @PathVariable("boardId") String id){
        try{
            Long boardId = Long.parseLong(id);
            return columnService.createColumn(column, boardId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }
    }
}
