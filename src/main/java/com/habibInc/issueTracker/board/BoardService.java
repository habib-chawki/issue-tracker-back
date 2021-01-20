package com.habibInc.issueTracker.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board createBoard(Board board){
        return boardRepository.save(board);
    }

    public Board getBoardById(Long id) {
        return null;
    }
}
