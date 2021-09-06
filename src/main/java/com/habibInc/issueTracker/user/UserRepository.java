package com.habibInc.issueTracker.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findAllByAssignedProjectsId(Long projectId, Pageable pageable);

    @Query(value = "SELECT * FROM user WHERE id NOT IN " +
                    "(SELECT user_id FROM project_user WHERE project_id = :projectId)", nativeQuery = true)
    List<User> findAllByAssignedProjectNot(@Param("projectId") Long projectId, Pageable pageable);

    List<User> findAll(Pageable pageable);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
