package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.project.Project;
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

        user.setFullName("first-last");
        user.setUserName("my_username");
        user.setEmail("my_email@email.com");
        user.setPassword("this is it");
    }

    @Test
    public void itShouldSaveUser() {
        User savedUserEntity = userRepository.save(user);

        // expect the userEntity to have been saved successfully with an auto generated id
        assertThat(savedUserEntity).isEqualToComparingOnlyGivenFields(user);
        assertThat(savedUserEntity.getId()).isNotNull().isPositive();
    }

    @Test
    public void itShouldFindUserById() {
        // given a userEntity is created
        User savedUserEntity = userRepository.save(user);

        // when the findById method is invoked
        Optional<User> response = userRepository.findById(savedUserEntity.getId());

        // then the response should be the proper userEntity
        assertThat(response.get()).isEqualTo(savedUserEntity);
    }

    @Test
    public void itShouldFindUserByEmail() {
        User savedUserEntity = userRepository.save(user);

        // when the findByEmail method is invoked
        Optional<User> response = userRepository.findByEmail(savedUserEntity.getEmail());

        // then the proper userEntity should be returned
        assertThat(response.get()).isEqualTo(savedUserEntity);
    }

    @Test
    public void itShouldFindAllUsersByProjectId() {
        // given a project
        Project project = new Project();
        project.setName("Project 01");

        // given a set of users

    }
}
