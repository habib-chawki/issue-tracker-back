package com.habibInc.issueTracker.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Set<User> findAllByAssignedProjectsId(Long projectId);

    List<User> findAll(Pageable pageable);
}
