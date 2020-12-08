package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

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
        user.setPassword("MyP@ssworD");
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
        User returnedUser = userService.getUserById(1L);

        // then the response should be the proper user
        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    public void whenUserDoesNotExist_itShouldReturnUserNotFoundError() {
        // given a "user not found" error message
        String errorMessage = "User not found";

        // when an incorrect id is used to fetch a user that does not exist
        when(userRepository.findById(10L)).thenThrow(new ResourceNotFoundException(errorMessage));

        // then a ResourceNotFoundException should be thrown
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(10L));
    }

    @Test
    public void itShouldGetUserByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // when getUserByEmail service method is invoked with the user email
        User returnedUser = userService.getUserByEmail(user.getEmail());

        // then the response should be the proper user
        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    public void itShouldHashUserPassword() {
        String hashedPassword = "xh4DeS$e@dt8u";

        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn(hashedPassword);
        when(userRepository.save(user)).thenReturn(user);

        // when the user is created
        User createdUser = userService.createUser(user);

        // then the password should be hashed
        assertThat(createdUser.getPassword()).isEqualTo(hashedPassword);
    }
}
