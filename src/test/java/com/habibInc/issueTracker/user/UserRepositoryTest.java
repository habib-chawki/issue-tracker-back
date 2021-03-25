package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

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
    public void itShouldFindAllUsersByAssignedProjectId() {
        // given a project
        Project project = new Project();
        project.setName("Project 01");

        // given a set of users
        List<User> users = (List<User>) userRepository.saveAll(
                List.of(
                        User.builder().id(1L).email("user1@email.com").password("pass1").userName("user1@email.com").build(),
                        User.builder().id(2L).email("user2@email.com").password("pass2").userName("user2@email.com").build(),
                        User.builder().id(3L).email("user3@email.com").password("pass3").userName("user3@email.com").build()
                )
        );

        // given the project is saved
        project.setAssignedUsers(new HashSet<>(users));
        project = projectRepository.save(project);

        // when a request to find users by project id is made
        Set<User> usersByProjectId = userRepository.findAllByAssignedProjectsId(project.getId());

        // then expect the response to be the project's dev team
        assertThat(usersByProjectId).containsExactlyElementsOf(users);
    }
}
