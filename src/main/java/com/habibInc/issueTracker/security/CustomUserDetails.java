package com.habibInc.issueTracker.security;

import com.habibInc.issueTracker.user.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final User authenticatedUser;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, com.habibInc.issueTracker.user.User authenticatedUser) {
        super(username, password, authorities);
        this.authenticatedUser = authenticatedUser;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
