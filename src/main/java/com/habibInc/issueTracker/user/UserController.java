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
import java.util.List;
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
        UserDto responseBody = modelMapper.map(createdUser, UserDto.class);

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


    @GetMapping(value = {"", "/"}, params = {"excludedProject", "page", "size"})
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getPaginatedListOfUsersNotAssignedToProject(@RequestParam(value = "excludedProject") Long excludedProjectId,
                                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size){
        return null;

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

    @GetMapping(value= {"/" , ""}, params = {"page", "size"})
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getPaginatedListOfUsers(@RequestParam(value = "page") int page,
                                                 @RequestParam(value = "size") int size) {
        // get the paginated list of users, invoke the service
        List<User> users = userService.getPaginatedListOfUsers(page, size);

        // map to DTOs
        List<UserDto> usersDto = users.stream().map((user) -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());

        return usersDto;
    }
}
