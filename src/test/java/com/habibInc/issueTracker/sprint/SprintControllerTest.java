package com.habibInc.issueTracker.sprint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SprintController.class)
@WithMockUser
public class SprintControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
            SprintService sprintService;

    Sprint sprint;

    @BeforeEach
    public void setup(){
        sprint = Sprint.builder()
                .id(1L)
                .name("First sprint")
                .goal("sprint goal")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();
    }

    @Test
    public void itShouldCreateSprint() throws Exception {
        // given the request body
        String requestBody = mapper.writeValueAsString(sprint);

        mockMvc.perform(post("/projects/1/sprints")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(requestBody));
    }
}
