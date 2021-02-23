package com.habibInc.issueTracker.sprint;

import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity

@Builder

public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String goal;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
