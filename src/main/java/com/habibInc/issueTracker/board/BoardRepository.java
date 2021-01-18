package com.habibInc.issueTracker.board;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends CrudRepository<Board, Long> {}
