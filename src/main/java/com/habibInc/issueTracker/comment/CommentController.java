package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issues/{id}/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@RequestBody Comment comment, @PathVariable String id){
        try{
            Long issueId = Long.parseLong(id);
            return commentService.createComment(comment, issueId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid issue id");
        }
    }
}
