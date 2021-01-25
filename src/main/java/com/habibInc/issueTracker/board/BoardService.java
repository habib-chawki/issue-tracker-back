package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board createBoard(Board board, User owner){
        board.setOwner(owner);
        return boardRepository.save(board);
    }

    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));
    }

    public void deleteBoardById(Long boardId) {
        // find board by id (throws board not found exception)
        Board board = this.getBoardById(boardId);

        boardRepository.deleteById(boardId);
    }
}
