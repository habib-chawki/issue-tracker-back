package com.habibInc.issueTracker.project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {
    Set<Project> findAllByAssignedUsersId(Long userId);
}
