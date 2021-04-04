package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
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
        user.setFullName("full name");
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
    public void whenUserCanNotBeFoundById_itShouldReturnUserNotFoundError() {
        // given a "user not found" error message
        String errorMessage = "User not found";

        // when an incorrect id is used to fetch a user that does not exist
        when(userRepository.findById(10L)).thenReturn(Optional.ofNullable(null));

        // then a ResourceNotFoundException should be thrown
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> userService.getUserById(10L))
                .withMessageContaining("User not found");
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
    public void whenUserCanNotBeFoundByEmail_itShouldReturnUserNotFoundError() {
        // set up an error message and an invalid email
        String errorMessage = "User not found";
        String invalidEmail = "user_does_not_exist@email.com";

        // when an incorrect email is used and the user does not exist
        when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.ofNullable(null));

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

        // when the user is created
        User createdUser = userService.createUser(user);

        // then the password should be hashed
        assertThat(createdUser.getPassword()).isEqualTo(hashedPassword);
    }

    @Test
    public void itShouldGetUsersByAssignedProject() {
        // given a project id
        Long projectId = 10L;

        // given a list of users
        Set<User> users = Set.of(
                User.builder().id(1L).userName("user1@email.com").build(),
                User.builder().id(2L).userName("user2@email.com").build(),
                User.builder().id(3L).userName("user3@email.com").build()
        );

        // given the repository response
        when(userRepository.findAllByAssignedProjectsId(projectId)).thenReturn(users);

        // when the service is invoked
        Set<User> usersByProject = userService.getUsersByAssignedProject(projectId);

        // then expect the list of users to have been fetched successfully
        assertThat(usersByProject).isEqualTo(users);

        verify(userRepository, times(1)).findAllByAssignedProjectsId(projectId);
    }

    @Test
    public void itShouldGetPaginatedListOfUsers() {
        // given the page and size
        int page = 0;
        int size = 3;

        // given a list of users
        List<User> users = List.of(
                User.builder().id(10L).userName("user01").build(),
                User.builder().id(20L).userName("user02").build(),
                User.builder().id(30L).userName("user03").build()
        );

        // given the repository response
        when(userRepository.findAll(PageRequest.of(page, size))).thenReturn(users);

        // when the service is invoked to get the paginated list of users
        List<User> paginatedListOfUsers = userService.getPaginatedListOfUsers(page, size);

        // then expect the paginated list to have been properly retrieved
        assertThat(paginatedListOfUsers).isEqualTo(users);
        verify(userRepository, times(1)).findAll(PageRequest.of(page, size));
    }

    @Test
    public void itShouldGetPaginatedListOfUsersNotAssignedToProject() {
        // given the excluded project id
        Long excludedProjectId = 777L;

        // given the page and size
        int page = 0;
        int size = 3;

        // given a list of users
        List<User> users = List.of(
                User.builder().id(10L).userName("user01").build(),
                User.builder().id(20L).userName("user02").build(),
                User.builder().id(30L).userName("user03").build()
        );

        // given the repository response
        when(userRepository.findAllByAssignedProjectsIsEmptyOrAssignedProjectsIdNot(excludedProjectId, PageRequest.of(page, size))).thenReturn(users);

        // when the service is invoked to retrieve the paginated list of users
        List<User> paginatedListOfUsersNotAssignedToProject =
                userService.getUsersNotAssignedToProject(excludedProjectId, page, size);

        // then expect the list of users not assigned to the given project to have been retrieved
        assertThat(paginatedListOfUsersNotAssignedToProject).isEqualTo(users);
        verify(userRepository, times(1)).findAllByAssignedProjectsIsEmptyOrAssignedProjectsIdNot(excludedProjectId, PageRequest.of(page, size));
    }
}
