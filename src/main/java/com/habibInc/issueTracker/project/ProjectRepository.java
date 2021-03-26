package com.habibInc.issueTracker.project;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {
    Set<Project> findAllByAssignedUsersId(Long userId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO project_user(user_id, project_id) VALUES(:userId, :projectId) ", nativeQuery = true)
    void addUserToProject(@Param(value = "userId") Long userId,
                          @Param(value = "projectId") Long projectId);
}
