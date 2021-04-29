package com.habibInc.issueTracker.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import com.habibInc.issueTracker.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    JwtUtil jwtUtil;

    @SpyBean
    ModelMapper modelMapper;

    User user;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .fullName("full")
                .userName("my_username")
                .email("my_email@email.com")
                .password("user_password")
                .build();
    }

    @Test
    public void itShouldSignUpUser() throws Exception {
        when(userService.createUser(user)).thenReturn(user);
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("auth_token");

        String requestBody = objectMapper.writeValueAsString(user);

        // given the expected response body
        UserDto responseBody = new ModelMapper().map(user, UserDto.class);

        // expect the response to be the user DTO
        mockMvc.perform(post("/users/signup")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responseBody)));
    }

    @Test
    public void whenUserIsSignedUp_itShouldRespondWithAuthorizationTokenHeader() throws Exception {
        // given the expected auth token
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbiI6InNvbWV0b2tlbiJ9.evnBwUTlQE0U2-hqZ3dUsrOoeb0y56sx_K9O0SbCs7Y";

        when(userService.createUser(user)).thenReturn(user);
        when(jwtUtil.generateToken(user.getEmail())).thenReturn(token);

        String requestBody = objectMapper.writeValueAsString(user);

        // when a user is signed up then expect the response to contain an auth token header
        mockMvc.perform(post("/users/signup")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists(JwtUtil.HEADER))
                .andExpect(header().string(JwtUtil.HEADER, JwtUtil.TOKEN_PREFIX + token));
    }

    @Test
    public void givenUserSignup_whenEmailIsInvalid_itShouldReturnInvalidEmailError() throws Exception {
        // given an invalid user email
        user.setEmail("invalid_email");

        // when a signup request with an invalid email is made
        String requestBody = objectMapper.writeValueAsString(user);

        // then a 400 invalid email error should be returned
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Email should be valid"));
    }

    @Test
    public void givenUserSignup_whenPasswordIsInvalid_itShouldReturnInvalidPasswordError() throws Exception {
        // given an invalid short user password
        user.setPassword("1a");

        // when a signup request with an invalid password is made
        String requestBody = objectMapper.writeValueAsString(user);

        // then a 400 invalid password error should be returned
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Password should be at least 5 characters long"));
    }

    @Test
    @WithMockUser
    public void itShouldGetUserById() throws Exception {
        // return a userEntity when the getUser service method is invoked
        when(userService.getUserById(1L)).thenReturn(user);

        // set up the perceived response body
        String responseBody = objectMapper.writeValueAsString(user);

        // expect the userEntity to have been returned successfully
        mockMvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseBody));
    }

    @Test
    @WithMockUser
    public void givenGetUserById_whenUserDoesNotExist_itShouldReturnUserNotFoundError() throws Exception {
        // given the expected error message
        String errorMessage = "User not found";

        // when the user does not exist
        when(userService.getUserById(10L)).thenThrow(new ResourceNotFoundException(errorMessage));

        // then the response should be a 404 user not found error
        mockMvc.perform(get("/users/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    @WithMockUser
    public void givenGetUserById_whenIdIsInvalid_itShouldReturnInvalidUserIdError() throws Exception {
        // given the expected error message
        String errorMessage = "Invalid user id";

        // when a GET request with an invalid user id is made
        // then a 400 error should be returned
        mockMvc.perform(get("/users/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    @WithMockUser
    public void itShouldGetUsersByAssignedProject() throws Exception {
        // given a project id
        Long projectId = 10L;

        // given the page and size params
        int page = 0;
        int size = 10;

        // given a list of users
        Set<User> users = Set.of(
                User.builder().id(1L).userName("user1@email.com").build(),
                User.builder().id(2L).userName("user2@email.com").build(),
                User.builder().id(3L).userName("user3@email.com").build()
        );

        // given the expected response
        String expectedResponse = objectMapper.writeValueAsString(
                users.stream().map(
                        user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList()
                )
        );

        // given the user service
        when(userService.getUsersByAssignedProject(projectId, page, size)).thenReturn(users);

        // when a GET request is made, then expect the response to be the list of users
        mockMvc.perform(get("/users?project=" + projectId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponse))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void itShouldGetPaginatedListOfUsers() throws Exception {
        // given a list of users
        List<User> users = List.of(
                User.builder().id(10L).userName("user01").build(),
                User.builder().id(20L).userName("user02").build(),
                User.builder().id(30L).userName("user03").build()
        );

        // given the service response
        when(userService.getPaginatedListOfUsers(0, users.size())).thenReturn(users);

        // given the expected response
        String expectedResponse = objectMapper.writeValueAsString(
                users.stream().map((user) -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList())
        );

        mockMvc.perform(get("/users?page=0&size=3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @WithMockUser
    public void itShouldGetPaginatedListOfUsersNotAssignedToProject() throws Exception {
        // given a project id
        Long excludedProjectId = 666L;

        // given a list of users
        List<User> users = List.of(
                User.builder().id(10L).userName("user01").build(),
                User.builder().id(20L).userName("user02").build(),
                User.builder().id(30L).userName("user03").build()
        );

        // given the service response
        when(userService.getUsersNotAssignedToProject(excludedProjectId, 0, users.size())).thenReturn(users);

        // given the expected response
        String expectedResponse = objectMapper.writeValueAsString(
                users.stream().map((user) -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList())
        );

        // when a GET request is made then expect the paginated list of users to be retrieved
        mockMvc.perform(get("/users")
                .param("excludedProject", String.valueOf(excludedProjectId))
                .param("page", "0")
                .param("size", String.valueOf(users.size())))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
