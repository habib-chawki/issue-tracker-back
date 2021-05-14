package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.project.Project;
import com.habibInc.issueTracker.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
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
                        User.builder().id(1L).email("user1@email.com").password("pass1").userName("user_01").fullName("user 01").build(),
                        User.builder().id(2L).email("user2@email.com").password("pass2").userName("user_02").fullName("user 02").build(),
                        User.builder().id(3L).email("user3@email.com").password("pass3").userName("user_03").fullName("user 03").build()
                )
        );

        // given the project is saved
        project.setAssignedUsers(new HashSet<>(users));
        project = projectRepository.save(project);

        // when a request to find users by project id is made
        List<User> usersByProjectId = userRepository.findAllByAssignedProjectsId(project.getId(), PageRequest.of(0, 10));

        // then expect the response to be the project's dev team
        assertThat(usersByProjectId).containsExactlyElementsOf(users);
    }

    @Test
    public void itShouldGetPaginatedListOfUsers() {
        // given a list of users
        List<User> users = List.of(
                User.builder().email("user01@email").password("user01pass").fullName("user 01").userName("user_01").build(),
                User.builder().email("user02@email").password("user02pass").fullName("user 02").userName("user_02").build(),
                User.builder().email("user03@email").password("user03pass").fullName("user 03").userName("user_03").build(),
                User.builder().email("user04@email").password("user04pass").fullName("user 04").userName("user_04").build(),
                User.builder().email("user05@email").password("user05pass").fullName("user 05").userName("user_05").build()
        );
        users = (List<User>) userRepository.saveAll(users);

        // given the pageable object
        int page = 0;
        int size = 3;
        PageRequest pageable = PageRequest.of(page, size);

        // when a request is made to find all users by page
        List<User> paginatedListOfUsers = userRepository.findAll(pageable);

        // then the response should be the list of paginated users
        assertThat(paginatedListOfUsers.size()).isEqualTo(size);

        // when a request is made to find all the users in the next page
        pageable = PageRequest.of(page + 1, size);
        paginatedListOfUsers = userRepository.findAll(pageable);

        // then expect only the users in that page to haven been retrieved
        assertThat(paginatedListOfUsers.size()).isEqualTo(users.size() - size);
    }

    @Test
    public void itShouldGetListOfPaginatedUsersNotAssignedToProject() {
        // given the list of users assigned to the project
        List<User> assignedUsers = List.of(
                User.builder().email("user01@email").password("user01pass").fullName("user 01").userName("user_01").build(),
                User.builder().email("user02@email").password("user02pass").fullName("user 02").userName("user_02").build(),
                User.builder().email("user03@email").password("user03pass").fullName("user 03").userName("user_03").build()
        );

        // given the lists of non-assigned users
        List<User> notAssignedUsers = List.of(
                User.builder().email("user04@email").password("user04pass").fullName("user 04").userName("user_04").build(),
                User.builder().email("user05@email").password("user05pass").fullName("user 05").userName("user_05").build()
        );

        List<User> notAssignedUsers2 = List.of(
                User.builder().email("user06@email").password("user06pass").fullName("user 06").userName("user_06").build(),
                User.builder().email("user07@email").password("user07pass").fullName("user 07").userName("user_07").build()
        );

        assignedUsers = (List<User>) userRepository.saveAll(assignedUsers);
        notAssignedUsers = (List<User>) userRepository.saveAll(notAssignedUsers);
        notAssignedUsers2 = (List<User>) userRepository.saveAll(notAssignedUsers2);

        // given the project that users are assigned to
        Project assignedProject = projectRepository.save(
                Project.builder().name("Assigned project").assignedUsers(new HashSet(assignedUsers)).build()
        );

        // given other projects
        Project otherProject = projectRepository.save(
                Project.builder().name("Other project 1").assignedUsers(new HashSet(notAssignedUsers)).build()
        );

        // given the page size
        int pageSize = notAssignedUsers.size() + 1;

        // when a request is made to find the users that are not assigned to the project
        List<User> usersNotAssignedToProject =
                userRepository.findAllByAssignedProjectNot(assignedProject.getId(), PageRequest.of(0, pageSize));

        // then expect only the users that are not assigned to the project to have been retrieved
        assertThat(usersNotAssignedToProject).doesNotContainAnyElementsOf(assignedUsers);
        assertThat(usersNotAssignedToProject).containsAnyElementsOf(notAssignedUsers);
        assertThat(usersNotAssignedToProject).containsAnyElementsOf(notAssignedUsers2);

        // expect the list to be paginated
        assertThat(usersNotAssignedToProject.size()).isEqualTo(pageSize);
    }

    @Test
    public void givenGetListOfPaginatedUsersNotAssignedToProject_itShouldGetUsersNotAssignedToAnyProject() {
        // given a list of users
        List<User> users = (List<User>) userRepository.saveAll(
                List.of(
                        User.builder().email("user01@email").password("user01pass").fullName("user 01").userName("user_01").build(),
                        User.builder().email("user02@email").password("user02pass").fullName("user 02").userName("user_02").build(),
                        User.builder().email("user03@email").password("user03pass").fullName("user 03").userName("user_03").build()
                )
        );

        // given a project
        Project project = projectRepository.save(Project.builder().name("Project 01").build());

        // when a request is made to find users not assigned to the project
        List<User> response = userRepository.findAllByAssignedProjectNot(project.getId(), PageRequest.of(0, users.size()));

        // then expect the response to be all the users
        assertThat(response).isEqualTo(users);
    }

    @Test
    public void givenExistsByEmail_whenUserDoesNotExist_itShouldReturnFalse() {
        String email = "doesNotExist@email";
        boolean existsByEmail = userRepository.existsByEmail(email);
        assertThat(existsByEmail).isFalse();
    }
}
