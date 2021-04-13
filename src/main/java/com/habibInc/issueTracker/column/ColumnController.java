package com.habibInc.issueTracker.column;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.issue.Issue;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/boards/{boardId}")

public class ColumnController {

    private final ColumnService columnService;

    @Autowired
    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }

    @PostMapping("/column")
    @ResponseStatus(HttpStatus.CREATED)
    public Column createColumn(@PathVariable("boardId") String id,
                               @RequestBody Column column
                               ){
        try{
            Long boardId = Long.parseLong(id);

            return columnService.createColumn(boardId, column);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }
    }

    @PostMapping("/columns")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Column> createColumns(@PathVariable("boardId") String id,
                                      @RequestBody List<Column> columns){
        try{
            Long boardId = Long.parseLong(id);

            return columnService.createColumns(boardId, columns);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid board id");
        }
    }

    @GetMapping("/columns/{columnId}")
    @ResponseStatus(HttpStatus.OK)
    public Column getColumnById(@PathVariable String boardId, @PathVariable String columnId) {
        try {
            Long parsedBoardId = Long.parseLong(boardId);
            Long parsedColumnId = Long.parseLong(columnId);

            return columnService.getColumnById(parsedBoardId, parsedColumnId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }

    @GetMapping(path = "/columns/{columnId}/issues")
    @ResponseStatus(HttpStatus.OK)
    public List<Issue> getPaginatedListOfIssues(@PathVariable String columnId,
                                                @PathVariable String boardId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        try{
            Long parsedColumnId = Long.parseLong(columnId);
            Long parsedBoardId = Long.parseLong(boardId);

            return columnService.getPaginatedListOfIssues(parsedBoardId, parsedColumnId, page, size);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }

    @DeleteMapping(path = "/columns/{columnId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteColumnById(@PathVariable String boardId,
                                 @PathVariable String columnId,
                                 @AuthenticationPrincipal User authenticatedUser) {
        try{
            Long parsedBoardId = Long.parseLong(boardId);
            Long parsedColumnId = Long.parseLong(columnId);

            columnService.deleteColumnById(parsedBoardId, parsedColumnId, authenticatedUser);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }

    @PatchMapping(path = "/columns/{columnId}")
    @ResponseStatus(HttpStatus.OK)
    public String updateColumnTitle(@RequestBody String request,
                                    @PathVariable String boardId,
                                    @PathVariable String columnId,
                                    @AuthenticationPrincipal User authenticatedUser) throws JsonProcessingException {
        try{
            final ObjectMapper mapper = new ObjectMapper();
            Map<String, String> body;

            // extract the new title from the request body
            body = mapper.readValue(request, Map.class);
            String title = body.get("newColumnTitle");

            // validate the board and column ids
            Long parsedBoardId = Long.parseLong(boardId);
            Long parsedColumnId = Long.parseLong(columnId);

            // invoke the service and get back the updated title
            String updatedTitle =
                    columnService.updateTitle(parsedBoardId, parsedColumnId, title, authenticatedUser);

            // setup the response body with the updated title
            body = new HashMap<>();
            body.put("updatedTitle", updatedTitle);

            return mapper.writeValueAsString(body);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid id");
        }
    }

    @PatchMapping("/columns/{columnId}/issues/{issueId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateIssueColumn(@RequestBody String request,
                                  @PathVariable Long boardId,
                                  @PathVariable Long columnId,
                                  @PathVariable Long issueId) throws JsonProcessingException {

        // extract the request body
        Map<String, String> requestBody = new ObjectMapper().readValue(request, Map.class);

        // extract and validate the new column id
        Long newColumnId = Utils.validateId(requestBody.get("newColumnId"));

        // update the issue column
        columnService.updateIssueColumn(boardId, columnId, issueId, newColumnId);
    }
}
