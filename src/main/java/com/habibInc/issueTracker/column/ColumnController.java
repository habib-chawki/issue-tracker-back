package com.habibInc.issueTracker.column;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards/{boardId}/columns")

public class ColumnController {
    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)

    public Column createColumn(@RequestBody Column column){
        return null;
    }
}
