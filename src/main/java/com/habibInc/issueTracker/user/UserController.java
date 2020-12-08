package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable String id){
        try{
            Long userId = Long.parseLong(id);
            return userService.getUserById(userId);
        }catch(NumberFormatException ex){
            throw new InvalidIdException("Invalid user id");
        }

    }
}
