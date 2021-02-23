package com.habibInc.issueTracker.sprint;

import com.habibInc.issueTracker.project.Project;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Builder

public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String goal;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne
    private Project project;
}
