package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.InvalidIdException;
import com.habibInc.issueTracker.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

        log.info("User signup success: {}", responseBody);

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
            final User userById = userService.getUserById(userId);

            log.info("Fetched user by id: {userId: {}}", id);

            return userById;
        }catch(NumberFormatException ex){
            log.error("Invalid user id: {userId: {}}", id);
            throw new InvalidIdException("Invalid user id");
        }
    }


    @GetMapping(value = {"", "/"}, params = {"excludedProject", "page", "size"})
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getPaginatedListOfUsersNotAssignedToProject(@RequestParam(value = "excludedProject") Long excludedProjectId,
                                                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size){
        // fetch the list of paginated users not assigned to project
        List<User> users = userService.getUsersNotAssignedToProject(excludedProjectId, page, size);

        // convert to UserDto
        List<UserDto> usersNotAssignedToProject =
                users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());

        log.info("List of users not assigned to project: {projectId: {}, users: {}, page: {}, pageSize: {}}", excludedProjectId, usersNotAssignedToProject, page, size);

        return usersNotAssignedToProject;
    }

    @GetMapping(value = {"/", ""}, params = {"project", "page", "size"})
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsersByAssignedProject(@RequestParam(value = "project") Long projectId,
                                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "10") int size) {
        // invoke service, fetch the paginated list of users
        List<User> users = userService.getUsersByAssignedProject(projectId, page, size);

        // convert to UserDto
        List<UserDto> usersByProject =
                users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());

        log.info("List of users assigned to project: {projectId: {}, users: {}, page: {}, pageSize: {}}", projectId, usersByProject, page, size);

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

        log.info("Paginated list of users: {users: {}, page: {}, pageSize: {}}", page, size, usersDto);

        return usersDto;
    }
}
