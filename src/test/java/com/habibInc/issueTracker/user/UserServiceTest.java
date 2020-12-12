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
        User createdUserEntity = userService.createUser(user);

        // then the userEntity should be successfully created
        assertThat(createdUserEntity).isEqualTo(user);
    }

    @Test
    public void itShouldGetUserById() {
        // given the repository returns a userEntity
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when the getUser service method is invoked with the userEntity id
        User returnedUserEntity = userService.getUserById(1L);

        // then the response should be the proper userEntity
        assertThat(returnedUserEntity).isEqualTo(user);
    }

    @Test
    public void whenUserCanNotBeFoundById_itShouldReturnUserNotFoundError() {
        // given a "user not found" error message
        String errorMessage = "User not found";

        // when an incorrect id is used to fetch a userEntity that does not exist
        when(userRepository.findById(10L))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        // then a ResourceNotFoundException should be thrown
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> userService.getUserById(10L))
                .withMessageContaining("User not found");
    }

    @Test
    public void itShouldGetUserByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // when getUserByEmail service method is invoked with the userEntity email
        User returnedUserEntity = userService.getUserByEmail(user.getEmail());

        // then the response should be the proper userEntity
        assertThat(returnedUserEntity).isEqualTo(user);
    }

    @Test
    public void whenUserCanNotBeFoundByEmail_itShouldReturnUserNotFoundError() {
        // set up an error message and an invalid email
        String errorMessage = "User can not be found by email";
        String invalidEmail = "user_does_not_exist@email.com";

        // when an incorrect email is used and the userEntity does not exist
        when(userRepository.findByEmail(invalidEmail))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        // then a ResourceNotFoundException should be thrown
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> userService.getUserByEmail(invalidEmail))
                .withMessageContaining(errorMessage);
    }

    @Test
    public void itShouldHashUserPassword() {
        String hashedPassword = "xh4DeS$e@dt8u";

        when(bCryptPasswordEncoder.encode(user.getPassword())).thenReturn(hashedPassword);
        when(userRepository.save(user)).thenReturn(user);

        // when the userEntity is created
        User createdUserEntity = userService.createUser(user);

        // then the password should be hashed
        assertThat(createdUserEntity.getPassword()).isEqualTo(hashedPassword);
    }
}
