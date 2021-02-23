package com.habibInc.issueTracker.sprint;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.LocalDateTime;

@WebMvcTest(SprintController.class)
public class SprintControllerTest {

    Sprint sprint;

    @BeforeEach
    public void setup(){
        sprint = Sprint.builder()
                .id(1L)
                .name("First sprint")
                .goal("sprint goal")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1)).build();
    }
}
