package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.ForbiddenOperationException;
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
        Issue issue = issueService.getIssue(issueId);

        // set the comment issue and owner
        comment.setIssue(issue);
        comment.setOwner(owner);

        return commentRepository.save(comment);
    }

    public Comment getCommentByIssueId(Long issueId) {
        return commentRepository.findByIssueId(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    public void deleteComment(Long issueId, Long commentId, User authenticatedUser) {
        // find the comment by its issue's id (ensure that both the issue and comment exist)
        Comment comment = getCommentByIssueId(issueId);

        // in case the owner is not the authenticated user, throw a forbidden operation error
        if(!comment.getOwner().equals(authenticatedUser))
            throw new ForbiddenOperationException("Forbidden");

        // delete the comment if it exists, otherwise a ResourceNotFoundException is thrown
        commentRepository.deleteById(commentId);
    }

}
