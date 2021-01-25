package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
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

    public Board createBoard(Board board, User authenticatedUser){
        board.setOwner(authenticatedUser);
        return boardRepository.save(board);
    }

    public Board getBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));
    }

    public void deleteBoardById(Long boardId, User authenticatedUser) {
        // find board by id (throws board not found exception)
        Board board = this.getBoardById(boardId);

        // check whether the authenticated user is the board owner or not
        if(!board.getOwner().equals(authenticatedUser)){
            throw new ForbiddenOperationException("Forbidden operation");
        }

        boardRepository.deleteById(boardId);
    }
}
