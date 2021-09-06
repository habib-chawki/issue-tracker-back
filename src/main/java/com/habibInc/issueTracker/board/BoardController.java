package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.user.User;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    private final ModelMapper modelMapper;

    @Autowired
    public BoardController(BoardService boardService, ModelMapper modelMapper){
        this.boardService = boardService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = {"", "/"}, params = "sprint")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardDto createBoard(@RequestParam(name = "sprint") Long sprintId,
                             @RequestBody Board board,
                             @AuthenticationPrincipal User authenticatedUser){
        Board createdBoard = boardService.createBoard(sprintId, board, authenticatedUser);

        // map board DTO
        final BoardDto createdBoardDto = modelMapper.map(createdBoard, BoardDto.class);

        log.info("Created board: {}", createdBoardDto);

        return createdBoardDto;
    }

    @GetMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public Board getBoard(@PathVariable("boardId") String id) {
        try{
            Long boardId = Long.parseLong(id);
            final Board boardById = boardService.getBoardById(boardId);

            log.info("Fetched board by id: {boardId: {}}", id);
            return boardById;
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }
    }

    @DeleteMapping("/{boardId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBoard(@PathVariable("boardId") String id,
                            @AuthenticationPrincipal User authenticatedUser) {
        try{
            Long boardId = Long.parseLong(id);
            boardService.deleteBoardById(boardId, authenticatedUser);
            log.info("Delete board by id: {boardId: {}, owner: {}}", id, authenticatedUser.getId());
        }catch (NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }

    }
}
