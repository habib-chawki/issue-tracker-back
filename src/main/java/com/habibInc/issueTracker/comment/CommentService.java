package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueService;
import com.habibInc.issueTracker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final IssueService issueService;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(IssueService issueService, CommentRepository commentRepository) {
        this.issueService = issueService;
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment, Long issueId, User owner) {
        // delegate call to issueService to get the issue by id (throws exception)
        issueService.getIssue(issueId);

        comment.setOwner(owner);
        return commentRepository.save(comment);

    }

    public void deleteComment(Long issueId, Long commentId) {
        Issue issue = issueService.getIssue(issueId);

        if (issue != null) {
        }

        throw new ResourceNotFoundException("Issue not found");
    }
}
