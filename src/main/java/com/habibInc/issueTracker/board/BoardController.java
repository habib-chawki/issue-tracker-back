package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService){
        this.boardService = boardService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Board createBoard(@RequestBody Board board){
        return boardService.createBoard(board);
    }

    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public Board getBoard(@PathVariable("boardId") String id) {
        try{
            Long boardId = Long.parseLong(id);
            return boardService.getBoardById(boardId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }
    }

}
