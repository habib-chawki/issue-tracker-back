package com.habibInc.issueTracker.column;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColumnRepository extends PagingAndSortingRepository<Column, Long> {}
