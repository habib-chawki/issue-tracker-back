package com.habibInc.issueTracker.user;

import com.habibInc.issueTracker.exceptionhandler.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User createUser(User user) {
        // hash user password before saving
        String userPassword = user.getPassword();
        user.setPassword(bCryptPasswordEncoder.encode(userPassword));

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User not found")
        );
    }

    public List<User> getPaginatedListOfUsers(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }
    public Set<User> getUsersByAssignedProject(Long projectId, int page, int size) {
        return userRepository.findAllByAssignedProjectsId(projectId, PageRequest.of(page, size));
    }

    public List<User> getUsersNotAssignedToProject(Long excludedProjectId, int page, int size) {
        return userRepository.findAllByAssignedProjectNot(excludedProjectId, PageRequest.of(page, size));
    }
}
