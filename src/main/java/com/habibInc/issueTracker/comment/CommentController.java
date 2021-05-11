package com.habibInc.issueTracker.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.user.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/issues/{issueId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final ModelMapper modelMapper;

    @Autowired
    public CommentController(CommentService commentService, ModelMapper modelMapper) {
        this.commentService = commentService;
        this.modelMapper = modelMapper;
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@RequestBody Comment comment,
                                 @PathVariable("issueId") String id,
                                 @AuthenticationPrincipal User owner){
        try{
            // verify issueId type
            Long issueId = Long.parseLong(id);
            Comment createdComment = commentService.createComment(comment, issueId, owner);

            // map comment DTO
            return modelMapper.map(createdComment, CommentDto.class);
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
            // verify issue and comment ids
            Long parsedCommentId = Long.parseLong(commentId);
            Long parsedIssueId = Long.parseLong(issueId);

            // delete comment after successful verification
            commentService.deleteComment(parsedIssueId, parsedCommentId, authenticatedUser);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public Comment updateComment(@PathVariable String commentId,
                              @PathVariable String issueId,
                              @RequestBody String request,
                              @AuthenticationPrincipal User authenticatedUser) throws JsonProcessingException {
        try{
            // extract comment content from request body
            Map<String, String> requestBody = new ObjectMapper().readValue(request, Map.class);
            String commentContent = requestBody.get("content");

            // verify ids and update comment
            Long parsedCommentId = Long.parseLong(commentId);
            Long parsedIssueId = Long.parseLong(issueId);
            return commentService.updateComment(parsedCommentId, parsedIssueId, commentContent, authenticatedUser);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }
}
