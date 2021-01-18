package com.habibInc.issueTracker.board;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
public class BoardController {

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Board createBoard(@RequestBody Board board){
        return board;
    }

}
