package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        HttpHeaders headers = new HttpHeaders();

        // generate auth token
        String token = jwtUtil.generateToken(user.getEmail());

        // set up auth header
        headers.add(JwtUtil.HEADER, token);

        // invoke the service to create the user
        User createdUser = userService.createUser(user);

        // set up the response with the auth token
        ResponseEntity<User> response = ResponseEntity.created(URI.create("/signup"))
                .header(JwtUtil.HEADER, token)
                .body(createdUser);

        return response;
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
