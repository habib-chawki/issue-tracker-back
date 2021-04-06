package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectRepository;
import com.habibInc.issueTracker.project.ProjectService;
import com.habibInc.issueTracker.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserIT {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    User authenticatedUser;

    String token;
    HttpHeaders headers;

    @BeforeEach
    public void setup() {
        // create a user
        authenticatedUser = new User();

        authenticatedUser.setFullName("first-last");
        authenticatedUser.setUserName("my_username");
        authenticatedUser.setEmail("my_email@email.com");
        authenticatedUser.setPassword("MyPassword");

        // authenticate the user
        headers = new HttpHeaders();
        token = jwtUtil.generateToken(authenticatedUser.getEmail());
        headers.add(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + token);
    }

    @Test
    public void itShouldSignUpUser() {
        ResponseEntity<UserDto> response =
                restTemplate.postForEntity("/users/signup", authenticatedUser, UserDto.class);

        // expect user to have been properly and successfully created
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // expect response to be the created user's id
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(authenticatedUser);
        assertThat(response.getBody().getId()).isNotNull().isPositive();
    }

    @Test
    public void whenUserIsSuccessfullySignedUp_itShouldResponseWithAuthTokenHeader() {
        ResponseEntity<UserDto> response =
                restTemplate.postForEntity("/users/signup", authenticatedUser, UserDto.class);

        // expect response to contain an auth token header
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().get(JwtUtil.HEADER)).toString().startsWith(JwtUtil.TOKEN_PREFIX);
    }

    @Test
    public void itShouldGetUserById() {
        // set up authorization header
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        // given a user is created
        User savedUser = userService.createUser(authenticatedUser);

        // when a get request is made to retrieve the user by id
        ResponseEntity<User> response = restTemplate.exchange(
                "/users/" + savedUser.getId(),
                HttpMethod.GET,
                httpEntity,
                User.class
        );

        // then the proper user should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualToComparingOnlyGivenFields(authenticatedUser);
        assertThat(response.getBody().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    public void givenGetUserById_whenUserDoesNotExist_itShouldReturnUserNotFoundError() {

        // save the user to pass the authorization filter successfully
        userService.createUser(authenticatedUser);

        // set up authorization header
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        // given an error message
        String errorMessage = "User not found";

        // when a post request with a user id that does not exist is made
        ResponseEntity<ApiError> response = restTemplate.exchange(
                "/users/404",
                HttpMethod.GET,
                httpEntity,
                ApiError.class
        );

        // then a 404 user not found error should be returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorMessage()).isEqualToIgnoringCase(errorMessage);
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    public void itShouldHashUserPassword() {
        // set up authorization header
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        // given a saved user
        User savedUser = userService.createUser(authenticatedUser);

        // when a get request is made with the saved user id
        ResponseEntity<User> response = restTemplate.exchange(
                "/users/" + savedUser.getId(),
                HttpMethod.GET,
                httpEntity,
                User.class
        );

        // then the password should be hashed and matches the plain text
        boolean match =
                bCryptPasswordEncoder.matches("MyPassword", savedUser.getPassword());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(match).isTrue();
    }

    @Test
    public void itShouldGetUsersByAssignedProject() {
        // given the authenticated user
        authenticatedUser = userService.createUser(authenticatedUser);

        // given the request
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        // given a project
        Project project = new Project();
        project.setName("Project 01");

        // given a set of users
        List<User> users = (List<User>) userRepository.saveAll(
                List.of(
                        User.builder().email("user1@email.com").password("pass1").userName("user1@email.com").build(),
                        User.builder().email("user2@email.com").password("pass2").userName("user2@email.com").build(),
                        User.builder().email("user3@email.com").password("pass3").userName("user3@email.com").build()
                )
        );

        // given the project is saved
        project.setAssignedUsers(new HashSet<>(users));
        project = projectRepository.save(project);

        // when a GET request is made to fetch the users by project
        ResponseEntity<UserDto[]> response =
                restTemplate.exchange("/users?project=" + project.getId(), HttpMethod.GET, httpEntity, UserDto[].class);

        // then expect the response to be the list of the project's dev team members
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isEqualTo(users.size());
    }

    @Test
    public void itShouldGetPaginatedListOfAllUsers() {
        // given the authenticated user
        authenticatedUser = userService.createUser(authenticatedUser);

        // given a list of users
        List<User> users = List.of(
                User.builder().email("user01@email").password("user01pass").build(),
                User.builder().email("user02@email").password("user02pass").build(),
                User.builder().email("user03@email").password("user03pass").build(),
                User.builder().email("user04@email").password("user04pass").build(),
                User.builder().email("user05@email").password("user05pass").build()
        );
        userRepository.saveAll(users);

        // given the request body
        HttpEntity httpEntity = new HttpEntity(headers);

        // given the page and size
        int page = 0;
        int size = 3;

        // given the endpoint url
        String url = "/users?page=" + page + "&size=" + size;

        // when a GET request is made to fetch a paginated list of users
        ResponseEntity<UserDto[]> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, UserDto[].class);

        // expect the response to be a paginated list of users
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isEqualTo(size);
    }

    @Test
    public void itShouldGetPaginatedListOfUsersNotAssignedToProject() {
        // given the authenticated user
        authenticatedUser = userService.createUser(authenticatedUser);

        // given the list of assigned users
        List<User> users = List.of(
                User.builder().email("user01@email").password("user01pass").build(),
                User.builder().email("user02@email").password("user02pass").build(),
                User.builder().email("user03@email").password("user03pass").build()
        );

        // given the list of non-assigned users
        List<User> otherUsers = List.of(
                User.builder().email("user04@email").password("user04pass").build(),
                User.builder().email("user05@email").password("user05pass").build()
        );

        users = (List<User>) userRepository.saveAll(users);
        otherUsers = (List<User>) userRepository.saveAll(otherUsers);

        // given the assigned project
        Project project = Project.builder().name("Assigned project").assignedUsers(new HashSet(users)).build();

        // given another project
        Project otherProject = Project.builder().name("Not assigned project").assignedUsers(new HashSet(otherUsers)).build();

        project = projectService.createProject(project, authenticatedUser);
        otherProject = projectService.createProject(otherProject, authenticatedUser);

        // given the pagination params
        int page = 0;
        int pageSize = 2;

        // given the request body
        HttpEntity httpEntity = new HttpEntity(headers);

        // given the url
        String url = "/users/?excludedProject=" + otherProject.getId() + "&page=" + page + "&size=" + pageSize;

        // given the expected response
        List<UserDto> expectedResponse = users.stream().map(user -> new ModelMapper().map(user, UserDto.class)).collect(Collectors.toList());

        // when a GET request is made
        ResponseEntity<UserDto[]> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, UserDto[].class);

        // then expect the response to be the paginated list of users not assigned to the project
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsAnyElementsOf(expectedResponse);
        assertThat(response.getBody().length).isEqualTo(pageSize);
    }

    @Test
    public void itShouldGetUserAssignedToAllButExcludedProject() {
        // given the authenticated user
        authenticatedUser = userService.createUser(authenticatedUser);

        // given a user
        User user = userService.createUser(User.builder().email("user@email.com").password("user_pass").build());

        // given projects
        Project excludedProject = Project.builder().name("Excluded project").build();
        Project includedProject1 = Project.builder().name("Included project 01").build();
        Project includedProject2 = Project.builder().name("Included project 02").build();
        Project includedProject3 = Project.builder().name("Included project 03").build();

        // given the excluded project is created by the authenticated user
        excludedProject = projectService.createProject(excludedProject, authenticatedUser);

        // given the other projects are saved
        projectRepository.saveAll(List.of(includedProject1, includedProject2, includedProject3));

        // given the user is added to all included projects
        projectService.addUserToProject(user.getId(), includedProject1.getId());
        projectService.addUserToProject(user.getId(), includedProject2.getId());
        projectService.addUserToProject(user.getId(), includedProject3.getId());

        // given the request body
        HttpEntity httpEntity = new HttpEntity(headers);

        // given the url
        String url = "/users/?excludedProject=" + excludedProject.getId() + "&page=" + 0 + "&size=" + 10;

        // given the expected response
        final UserDto expectedResponse = new ModelMapper().map(user, UserDto.class);

        // when a GET request is made
        ResponseEntity<UserDto[]> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, UserDto[].class);

        // then expect the response to be the user assigned to all but the excluded project
        assertThat(response.getBody()).containsExactly(expectedResponse);
    }


    @Test
    public void itShouldGetUsersAssignedToOtherProjectsButNotToExcludedProject() {
        // given the authenticated user
        authenticatedUser = userService.createUser(authenticatedUser);

        // given users
        User user1 = User.builder().email("user1@email").password("pass1").build();
        User user2 = User.builder().email("user2@email").password("pass2").build();
        User user3 = User.builder().email("user3@email").password("pass3").build();

        user1 = userService.createUser(user1);
        user2 = userService.createUser(user2);
        user3 = userService.createUser(user3);

        // given projects
        Project project1 = Project.builder().name("project 1").build();
        Project project2 = Project.builder().name("project 2").build();

        project1 = projectService.createProject(project1, authenticatedUser);
        project2 = projectService.createProject(project2, authenticatedUser);

        // given users assigned to projects
        projectService.addUserToProject(user1.getId(), project1.getId());
        projectService.addUserToProject(user2.getId(), project1.getId());

        projectService.addUserToProject(user2.getId(), project2.getId());
        projectService.addUserToProject(user3.getId(), project2.getId());

        // given the request body
        HttpEntity httpEntity = new HttpEntity(headers);

        // given the project 2 users
        ModelMapper modelMapper = new ModelMapper();
        List<UserDto> project2Users = List.of(
                modelMapper.map(user2, UserDto.class),
                modelMapper.map(user3, UserDto.class)
        );

        // when a GET request is made to fetch users not belonging to project 2
        String url = "/users/?excludedProject=" + project2.getId() + "&page=" + 0 + "&size=" + 10;
        ResponseEntity<UserDto[]> response =
                restTemplate.exchange(url, HttpMethod.GET, httpEntity, UserDto[].class);

        // then the response should be the users of project 1
        assertThat(response.getBody()).doesNotContainAnyElementsOf(project2Users);

        // given the project 1 users
        List<UserDto> project1Users = List.of(
                modelMapper.map(user1, UserDto.class),
                modelMapper.map(user2, UserDto.class)
        );

        // when a GET request is made to fetch users not belonging to project 1
        url = "/users/?excludedProject=" + project1.getId() + "&page=" + 0 + "&size=" + 10;
        response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, UserDto[].class);

        // then the response should be the users of project 2
        assertThat(response.getBody()).doesNotContainAnyElementsOf(project1Users);
    }

    @AfterEach
    public void teardown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }
}
