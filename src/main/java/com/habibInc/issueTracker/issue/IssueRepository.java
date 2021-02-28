package com.habibInc.issueTracker.issue;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends PagingAndSortingRepository<Issue, Long> {
    List<Issue> findByColumnId(Long columnId, Pageable pageable);
    List<Issue> findAllByProjectId(Long projectId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE issue SET sprint_id = :sprintId WHERE id IN :ids", nativeQuery = true)
    int setSprintBacklog(@Param("sprintId") Long sprintId, @Param("ids") List<Long> ids);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE issue SET `column_id` = :columnId WHERE id = :issueId", nativeQuery = true)
    int updateIssueColumn(@Param("issueId") Long issueId, @Param("columnId") Long columnId);
}
