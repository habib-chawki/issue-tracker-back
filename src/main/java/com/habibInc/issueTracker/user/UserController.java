package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.security.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, ModelMapper modelMapper) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid User user) {
        HttpHeaders headers = new HttpHeaders();

        // generate auth token and add 'Bearer ' prefix
        String token = JwtUtil.TOKEN_PREFIX + jwtUtil.generateToken(user.getEmail());

        // set up Authorization header
        headers.add(JwtUtil.HEADER, token);

        // invoke the service to create the user
        User createdUser = userService.createUser(user);

        // set up user DTO response body
        UserDto responseBody = new ModelMapper().map(createdUser, UserDto.class);

        // set up the response with the auth token
        ResponseEntity<UserDto> response = ResponseEntity.created(URI.create("/signup"))
                .header(JwtUtil.HEADER, token)
                .body(responseBody);

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

    @GetMapping(value = {"/", ""}, params = "project")
    @ResponseStatus(HttpStatus.OK)
    public Set<UserDto> getUsersByAssignedProject(@RequestParam(value = "project") Long projectId) {
        // invoke service, fetch the list of users
        Set<User> users = userService.getUsersByAssignedProject(projectId);

        // convert to UserDto
        Set<UserDto> usersByProject =
                users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toSet());

        return usersByProject;
    }
}
