package com.habibInc.issueTracker.security;

import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

public class UserDetailsServiceTest {

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @Mock
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        user = new User();

        user.setId(1L);
        user.setFullName("first");
        user.setUsername("my_username");
        user.setEmail("my_email@email.com");
        user.setPassword("MyP@ssworD");
    }

    @Test
    public void whenLoadUserByUsernameIsCalled_itShouldLoadUserByEmail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // when loadUserByUsername is invoked with a user email
        UserDetails loadedUser =
                userDetailsService.loadUserByUsername(user.getEmail());

        // then the proper user should be loaded
        assertThat(loadedUser.getUsername()).isEqualTo(user.getEmail());
        assertThat(loadedUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    public void whenLoadUserByUsernameReturnsNull_thenReturnUserNotFoundError() {
        // given an incorrect email of a user that does not exist
        String incorrectEmail = "userNotFound@email.com";

        // when the userRepository#findByEmail method is invoked
        when(userRepository.findByEmail(incorrectEmail))
                .thenReturn(Optional.ofNullable(null));

        // then a username not found error should be returned
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(() -> userDetailsService.loadUserByUsername(incorrectEmail))
                .withMessageContaining("User not found");
    }
}
