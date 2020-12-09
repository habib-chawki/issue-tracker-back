package com.habibInc.issueTracker.security;

import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) {
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            User loadedUser = userOptional.get();

            return new CustomUserDetails(
                    loadedUser.getEmail(),
                    loadedUser.getPassword(),
                    new ArrayList<>(),
                    loadedUser
            );
        }
        throw new UsernameNotFoundException("Invalid credentials");
    }
}
