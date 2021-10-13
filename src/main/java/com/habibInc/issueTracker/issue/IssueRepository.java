package com.habibInc.issueTracker.issue;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IssueRepository extends PagingAndSortingRepository<Issue, Long> {
    List<Issue> findByColumnId(Long columnId, Pageable pageable);
    List<Issue> findAllByProjectId(Long projectId);
    List<Issue> findAllByProjectIdAndSprintId(Long projectId, Long sprintId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE issue SET sprint_id = :sprintId WHERE id IN :ids", nativeQuery = true)
    int updateIssuesSprint(@Param("sprintId") Long sprintId, @Param("ids") List<Long> ids);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE issue SET `column_id` = :columnId WHERE id = :issueId", nativeQuery = true)
    int updateIssueColumn(@Param("issueId") Long issueId, @Param("columnId") Long columnId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE issue SET `column_id` = :columnId WHERE id IN :ids", nativeQuery = true)
    int updateIssuesColumn(@Param("columnId") Long columnId, @Param("ids") List<Long> ids);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "SET @tempId1 = (SELECT position FROM issue WHERE id = :id1); SET @tempId2 = (SELECT position FROM issue WHERE id = :id2); UPDATE issue SET position = (CASE WHEN id = :id1 THEN @tempId2 WHEN id = :id2 THEN @tempId1 END)", nativeQuery = true)
    void swapPositions(@Param("id1") Long issueId1, @Param("id2") Long issueId2);

    int countByProjectId(Long projectId);
}
