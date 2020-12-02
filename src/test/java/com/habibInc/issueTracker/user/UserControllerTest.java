package com.habibInc.issueTracker.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
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
        user = new User();

        user.setId(1L);
        user.setFirstName("first");
        user.setLastName("last");
        user.setUserName("my_username");
        user.setEmail("my_email@email.com");
        user.setPassword("this is it");
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
    @WithMockUser
    public void itShouldGetUserById() throws Exception {
        // return a user when the getUser service method is invoked
        when(userService.getUser(1L)).thenReturn(user);

        // set up the perceived response body
        String responseBody = mapper.writeValueAsString(user);

        // expect the user to have been returned successfully
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
        String errorMessage = "User not found";

        // when the user does not exist
        when(userService.getUser(10L)).thenThrow(new ResourceNotFoundException(errorMessage));

        // then the response should be a 404 user not found error
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

        // when a get request with an invalid user id is received
        // then a 400 error should be returned
        mockMvc.perform(get("/users/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value(errorMessage));
    }
}
