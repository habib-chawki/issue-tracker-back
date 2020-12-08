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
public class UserEntityRepositoryTest {
    @Autowired
    UserRepository userRepository;

    UserEntity userEntity;

    @BeforeEach
    public void setup() {
        userEntity = new UserEntity();

        userEntity.setFirstName("first");
        userEntity.setLastName("last");
        userEntity.setUserName("my_username");
        userEntity.setEmail("my_email@email.com");
        userEntity.setPassword("this is it");
    }

    @Test
    public void itShouldSaveUser() {
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // expect the userEntity to have been saved successfully with an auto generated id
        assertThat(savedUserEntity).isEqualToComparingOnlyGivenFields(userEntity);
        assertThat(savedUserEntity.getId()).isNotNull().isPositive();
    }

    @Test
    public void itShouldFindUserById() {
        // given a userEntity is created
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // when the findById method is invoked
        Optional<UserEntity> response = userRepository.findById(savedUserEntity.getId());

        // then the response should be the proper userEntity
        assertThat(response.get()).isEqualTo(savedUserEntity);
    }

    @Test
    public void itShouldFindUserByEmail() {
        UserEntity savedUserEntity = userRepository.save(userEntity);

        // when the findByEmail method is invoked
        Optional<UserEntity> response = userRepository.findByEmail(savedUserEntity.getEmail());

        // then the proper userEntity should be returned
        assertThat(response.get()).isEqualTo(savedUserEntity);
    }
}
