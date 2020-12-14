package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issues/{issueId}/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@RequestBody Comment comment,
                                 @PathVariable("issueId") String id,
                                 @AuthenticationPrincipal User owner){
        try{
            // verify issueId type
            Long issueId = Long.parseLong(id);
            return commentService.createComment(comment, issueId, owner);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid issue id");
        }
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@PathVariable String commentId,
                              @PathVariable String issueId,
                              @AuthenticationPrincipal User authenticatedUser){
        try{
            Long parsedCommentId = Long.parseLong(commentId);
            Long parsedIssueId = Long.parseLong(issueId);
            commentService.deleteComment(parsedIssueId, parsedCommentId, authenticatedUser);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateComment(@PathVariable String commentId){

    }
}
