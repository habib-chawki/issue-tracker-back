package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.issue.Issue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnRepository extends PagingAndSortingRepository<Column, Long> {
    List<Issue> findAllIssues(Pageable pageable);
}
