package com.habibInc.issueTracker.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class UserIT {
    @Autowired
    UserRepository userRepository;

    @Autowired
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

    @AfterEach
    public void teardown() {
        userRepository.deleteAll();
    }
}
