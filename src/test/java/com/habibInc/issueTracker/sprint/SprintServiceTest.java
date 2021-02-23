package com.habibInc.issueTracker.sprint;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SprintServiceTest {
    @InjectMocks
    SprintService sprintService;

    @Mock
    SprintRepository sprintRepository;

    Sprint sprint;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    public void setup() {
        sprint = Sprint.builder()
                .id(1L)
                .name("First sprint")
                .goal("sprint goal")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();
    }

    @Test
    public void itShouldCreateSprint() {
        // given the repository response
        when(sprintRepository.save(sprint)).thenReturn(sprint);

        // when sprintService#createSprint() is invoked
        Sprint createdSprint = sprintService.createSprint(sprint);

        // expect the repository to have been invoked
        verify(sprintRepository, times(1)).save(sprint);

        // then expect the sprint to have been created successfully
        assertThat(createdSprint).isEqualTo(sprint);
    }
}
