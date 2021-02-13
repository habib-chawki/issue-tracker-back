package com.habibInc.issueTracker.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    User user;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .firstName("first")
                .lastName("last")
                .userName("my_username")
                .email("my_email@email.com")
                .password("this is it")
                .build();
    }

    @Test
    public void itShouldSignUpUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user);

        String requestBody = mapper.writeValueAsString(user);

        mockMvc.perform(post("/users/signup")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(requestBody));
    }

    @Test
    public void givenUserSignup_whenEmailIsInvalid_itShouldReturnInvalidEmailError() throws Exception {
        // given an invalid user email
        user.setEmail("invalid_email");

        // when a signup request with an invalid email is made
        String requestBody = mapper.writeValueAsString(user);

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
        String requestBody = mapper.writeValueAsString(user);

        // then a 400 invalid password error should be returned
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void itShouldGetUserById() throws Exception {
        // return a userEntity when the getUser service method is invoked
        when(userService.getUserById(1L)).thenReturn(user);

        // set up the perceived response body
        String responseBody = mapper.writeValueAsString(user);

        // expect the userEntity to have been returned successfully
        mockMvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseBody));
    }

    @Test
    @WithMockUser
    public void itShouldReturnUserNotFoundError() throws Exception {
        // given an error message
        String errorMessage = "UserEntity not found";

        // when the userEntity does not exist
        when(userService.getUserById(10L)).thenThrow(new ResourceNotFoundException(errorMessage));

        // then the response should be a 404 userEntity not found error
        mockMvc.perform(get("/users/10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }

    @Test
    @WithMockUser
    public void itShouldReturnInvalidUserIdError() throws Exception {
        // given an error message
        String errorMessage = "Invalid user id";

        // when a get request with an invalid userEntity id is received
        // then a 400 error should be returned
        mockMvc.perform(get("/users/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }
}
