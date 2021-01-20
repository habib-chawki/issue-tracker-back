package com.habibInc.issueTracker.issue;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends PagingAndSortingRepository<Issue, Long> {
    List<Issue> findByColumnId(Long columnId, Pageable pageable);
}
