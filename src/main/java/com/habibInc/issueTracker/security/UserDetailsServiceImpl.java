package com.habibInc.issueTracker.security;

import com.habibInc.issueTracker.user.User;
import com.habibInc.issueTracker.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) {
        User loadedUser = userRepository.findByEmail(userEmail);

        if(loadedUser != null)
            return new org.springframework.security.core.userdetails.User(
                    loadedUser.getEmail(),
                    loadedUser.getPassword(),
                    new ArrayList<>());

        throw new UsernameNotFoundException("Incorrect credentials");
    }
}
