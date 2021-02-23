package com.habibInc.issueTracker.sprint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @Test
    public void itShouldGetSprintById() {
        // given the repository response
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));

        // when the sprint service is invoked to fetch a sprint by id
        Sprint retrievedSprint = sprintService.getSprintById(sprint.getId());

        // then the repository should be invoked and the sprint should be fetched successfully
        verify(sprintRepository, times(1)).findById(sprint.getId());
        assertThat(retrievedSprint).isEqualTo(sprint);
    }

    @Test
    public void itShouldAddIssuesToSprint() {
        fail("Failed intentionally");
    }
}
