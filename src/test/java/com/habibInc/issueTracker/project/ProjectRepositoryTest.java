package com.habibInc.issueTracker.project;

import com.habibInc.issueTracker.issue.IssueRepository;
import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ProjectRepositoryTest {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    UserRepository userRepository;

    Project project, project2;

    @BeforeEach
    public void setup() {
        project = new Project();
        project.setName("Primary project");

        project2 = new Project();
        project2.setName("Secondary project");
    }

    @Test
    public void itShouldSaveProject() {
        // when a project is saved
        Project savedProject = projectRepository.save(project);

        // then expect the id to be autogenerated and the response is the saved project
        assertThat(savedProject.getId()).isNotNull().isPositive();
        assertThat(savedProject).isEqualToComparingOnlyGivenFields(project);
    }

    @Test
    public void itShouldFindAllProjects() {
        List<Project> projects = List.of(project, project2);
        projectRepository.saveAll(projects);

        List<Project> retrievedProjects = (List<Project>) projectRepository.findAll();

        assertThat(retrievedProjects).isEqualTo(projects);
    }

    @Test
    public void itShouldFindAllProjectsByAssignedUserId() {
        // given a user
        User user = new User();
        user.setEmail("user@email.com");
        user.setPassword("user_pass");
        user.setFullName("user full");
        user.setUsername("user name");

        user = userRepository.save(user);

        // given a set of projects
        List<Project> projects = (List<Project>) projectRepository.saveAll(
                List.of(
                        Project.builder().name("Project 01").assignedUsers(Set.of(user)).build(),
                        Project.builder().name("Project 02").assignedUsers(Set.of(user)).build(),
                        Project.builder().name("Project 03").assignedUsers(Set.of(user)).build()
                )
        );

        // when a request is made to find all projects by assigned user
        Set<Project> projectsByUserId = projectRepository.findAllByAssignedUsersId(user.getId());

        // then expect the projects of the given user to have been fetched successfully
        assertThat(projectsByUserId).containsExactlyElementsOf(projects);
    }

    @Test
    public void itShouldAddUserToProject() {
        // given a user
        User user = new User();
        user.setEmail("user@email.com");
        user.setPassword("user_pass");
        user.setFullName("user full");
        user.setUsername("user name");

        user = userRepository.save(user);

        // given the project is saved
        project = projectRepository.save(project);

        // when add user to project is invoked
        projectRepository.addUserToProject(user.getId(), project.getId());

        // then expect the user to have been added to the project successfully
        Project updatedProject = projectRepository.findById(project.getId()).get();
        User updatedUser = userRepository.findById(user.getId()).get();

        assertThat(updatedUser.getAssignedProjects()).contains(project);
        assertThat(updatedProject.getAssignedUsers()).contains(user);
    }

    @Test
    public void itShouldRemoveUserFromProject() {
        // given a user
        User user = userRepository.save(
                User.builder()
                .username("username")
                .fullName("full name")
                .email("user@email.me")
                .password("user@pass")
                .build()
        );

        // given the project is saved
        project = projectRepository.save(project);

        // given the user is added to the project
        projectRepository.addUserToProject(user.getId(), project.getId());

        // expect the user to have been added successfully
        assertThat(projectRepository.findAllByAssignedUsersId(user.getId()))
                .containsExactly(project);

        // when a request to remove the user from the project is made
        projectRepository.removeUserFromProject(user.getId(), project.getId());

        // then expect the user to have been removed successfully
        assertThat(projectRepository.findAllByAssignedUsersId(user.getId()))
                .doesNotContain(project);
    }
}
