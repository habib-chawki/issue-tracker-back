package com.habibInc.issueTracker.issue;

import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
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

    public Issue getIssueById(Long id) {
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

    public Issue updateIssue(Long issueId, Issue issue, User authenticatedUser) {
        // make sure the issue already exists, otherwise an exception is thrown
        Issue issueToUpdate = getIssueById(issueId);

        // save the issue only if authenticated user is the reporter
        if (issueToUpdate.getReporter().equals(authenticatedUser))
            return issueRepository.save(issue);

        // in case the authenticated user is not the reporter, throw a forbidden error
        throw new ForbiddenOperationException("Forbidden");
    }

    public void deleteIssue(Long issueId, User authenticatedUser) {
        Issue issueToDelete = getIssueById(issueId);

        // in case the authenticated user is not the reporter, throw a forbidden error
        if (issueToDelete.getReporter().equals(authenticatedUser))
            issueRepository.deleteById(issueId);
        else
            throw new ForbiddenOperationException("Forbidden");
    }
}
