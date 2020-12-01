package com.habibInc.issueTracker.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    public void setup() {
        user = new User();

        user.setFirstName("first");
        user.setLastName("last");
        user.setUserName("my_username");
        user.setEmail("my_email@email.com");
        user.setPassword("this is it");
    }

    @Test
    public void itShouldSaveUser() {
        User savedUser = userRepository.save(user);

        // expect the user to have been saved successfully with an auto generated id
        assertThat(savedUser).isEqualToComparingOnlyGivenFields(user);
        assertThat(savedUser.getId()).isNotNull().isPositive();
    }

    @Test
    public void itShouldFindUserById() {
        // given a user is created
        User savedUser = userRepository.save(user);

        // when the findById method is invoked
        Optional<User> response = userRepository.findById(savedUser.getId());

        // then the response should be the proper user
        assertThat(response.get()).isEqualTo(savedUser);
    }
}
