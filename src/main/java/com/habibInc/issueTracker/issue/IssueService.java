package com.habibInc.issueTracker.issue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {
    @Autowired
    IssueRepository issueRepository;

    public Issue getIssue(Long id) {
        return issueRepository.findById(id).get();
    }

    public Issue createIssue(Issue issue) {
        return issueRepository.save(issue);
    }

    public Iterable<Issue> getAllIssues() {
        return issueRepository.findAll();
    }
}
