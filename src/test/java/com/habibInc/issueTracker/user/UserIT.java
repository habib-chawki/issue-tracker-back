package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectRepository;
import com.habibInc.issueTracker.project.ProjectService;
import com.habibInc.issueTracker.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.List;

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
        project = projectService.createProject(project, authenticatedUser);

        // when a GET request is made to fetch the users by project
        ResponseEntity<UserDto[]> response =
                restTemplate.exchange("/users?project=" + project.getId(), HttpMethod.GET, httpEntity, UserDto[].class);

        // then expect the response to be the list of the project's dev team members
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().length).isEqualTo(users.size());
    }

    @Test
    public void itShouldGetPaginatedListOfUsers() {
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

    @AfterEach
    public void teardown() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }
}
