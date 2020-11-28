package com.habibInc.issueTracker.comment;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issues/{id}/comments")
public class CommentController {
    @Autowired
    CommentService commentService;

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@RequestBody Comment comment, @PathVariable String id){
        try{
            Long.parseLong(id);
            return commentService.createComment(comment);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid issue id");
        }
    }
}
