package com.habibInc.issueTracker.sprint;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends CrudRepository<Sprint, Long> {
    List<Sprint> findAllByStatus(SprintStatus status);
}
