package com.habibInc.issueTracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationFilter.class)
public class AuthenticationFilterTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void itShouldLoginUser() throws Exception {
        mockMvc.perform(post("/users/login")).andExpect(status().isOk());
    }
}
