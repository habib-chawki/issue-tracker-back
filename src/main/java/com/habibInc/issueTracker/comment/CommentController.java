package com.habibInc.issueTracker.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    CommentService commentService;

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public Comment createComment(@RequestBody Comment comment){
        return commentService.createComment(comment);
    }
}
