package com.habibInc.issueTracker.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setEmail("my_email@email.com");
    }

    @Test
    public void itShouldCreateUser() {
//        when(userRepository.save(user)).thenReturn(user);
    }
}
