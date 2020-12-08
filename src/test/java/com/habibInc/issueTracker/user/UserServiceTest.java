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

public class UserEntityServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    UserEntity userEntity;

    @BeforeEach
    public void init() {
        initMocks(this);
    }

    @BeforeEach
    public void setup() {
        userEntity = new UserEntity();

        userEntity.setId(1L);
        userEntity.setFirstName("first");
        userEntity.setLastName("last");
        userEntity.setUserName("my_username");
        userEntity.setEmail("my_email@email.com");
        userEntity.setPassword("MyP@ssworD");
    }

    @Test
    public void itShouldCreateUser() {
        // given a call to the userRepository save method
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // when the service method createUser is invoked
        UserEntity createdUserEntity = userService.createUser(userEntity);

        // then the userEntity should be successfully created
        assertThat(createdUserEntity).isEqualTo(userEntity);
    }

    @Test
    public void itShouldGetUserById() {
        // given the repository returns a userEntity
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // when the getUser service method is invoked with the userEntity id
        UserEntity returnedUserEntity = userService.getUserById(1L);

        // then the response should be the proper userEntity
        assertThat(returnedUserEntity).isEqualTo(userEntity);
    }

    @Test
    public void whenUserCanNotBeFoundById_itShouldReturnUserNotFoundError() {
        // given a "userEntity not found" error message
        String errorMessage = "UserEntity can not be found by id";

        // when an incorrect id is used to fetch a userEntity that does not exist
        when(userRepository.findById(10L)).thenThrow(new ResourceNotFoundException(errorMessage));

        // then a ResourceNotFoundException should be thrown
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(10L));
    }

    @Test
    public void itShouldGetUserByEmail() {
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));

        // when getUserByEmail service method is invoked with the userEntity email
        UserEntity returnedUserEntity = userService.getUserByEmail(userEntity.getEmail());

        // then the response should be the proper userEntity
        assertThat(returnedUserEntity).isEqualTo(userEntity);
    }

    @Test
    public void whenUserCanNotBeFoundByEmail_itShouldReturnUserNotFoundError() {
        // set up an error message and an invalid email
        String errorMessage = "UserEntity can not be found by email";
        String invalidEmail = "user_does_not_exist@email.com";

        // when an incorrect email is used and the userEntity does not exist
        when(userRepository.findByEmail(invalidEmail))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        // then a ResourceNotFoundException should be thrown
        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByEmail(invalidEmail));
    }

    @Test
    public void itShouldHashUserPassword() {
        String hashedPassword = "xh4DeS$e@dt8u";

        when(bCryptPasswordEncoder.encode(userEntity.getPassword())).thenReturn(hashedPassword);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // when the userEntity is created
        UserEntity createdUserEntity = userService.createUser(userEntity);

        // then the password should be hashed
        assertThat(createdUserEntity.getPassword()).isEqualTo(hashedPassword);
    }
}
