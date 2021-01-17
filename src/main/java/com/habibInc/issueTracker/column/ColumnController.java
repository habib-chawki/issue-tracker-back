package com.habibInc.issueTracker.column;

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

    public Column createColumn(@RequestBody Column column){
        return columnService.createColumn(column);
    }
}
