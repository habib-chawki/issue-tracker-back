package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

    private final IssueRepository issueRepository;

    @Autowired
    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public Issue getIssue(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found"));
    }

    public Issue createIssue(Issue issue, User reporter) {
        issue.setReporter(reporter);
        return issueRepository.save(issue);
    }

    public Iterable<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    public Issue updateIssue(Issue issue) {
        return null;
    }
}
