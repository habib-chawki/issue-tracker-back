package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.ApiError;
import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void init() {
        initMocks(this);
    }

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
    public void itShouldCreateUser() {
        // given a call to the userRepository save method
        when(userRepository.save(user)).thenReturn(user);

        // when the service method createUser is invoked
        User createdUser = userService.createUser(user);

        // then the user should be successfully created
        assertThat(createdUser).isEqualTo(user);
    }

    @Test
    public void itShouldGetUserById() {
        // given the repository returns a user
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when the getUser service method is invoked with the user id
        User returnedUser = userService.getUser(1L);

        // then the response should be the proper user
        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    public void itShouldReturnUserNotFoundError() {
        String errorMessage = "User not found";

        when(userRepository.findById(10L)).thenThrow(new ResourceNotFoundException(errorMessage));

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUser(10L);
        });
    }

    @Test
    public void itShouldEncryptUserPassword() {
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertThat(createdUser.getPassword()).isNotEqualTo(user.getPassword());
    }
}
