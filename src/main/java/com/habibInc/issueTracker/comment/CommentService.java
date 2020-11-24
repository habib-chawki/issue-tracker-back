package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.issue.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private IssueService issueService;
    private CommentRepository commentRepository;

    @Autowired
    public CommentService(IssueService issueService, CommentRepository commentRepository){
        this.issueService = issueService;
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment) {
        Issue issue;

        // get issue id if issue is not null
        Long issueId =
                comment.getIssue() != null ? comment.getIssue().getId() : null;

        if(issueId != null) {
            // delegate call to issueService to get the issue by id
            issue = issueService.getIssue(issueId);

            // save the comment if the issue is present
            if (issue != null)
                return commentRepository.save(comment);
        }

        throw new ResourceNotFoundException("Issue not found");
    }
}
