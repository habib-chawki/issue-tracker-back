package com.habibInc.issueTracker.column;

import lombok.*;

import javax.persistence.*;

@Entity

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder

@Table(name = "`column`")
public class Column {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
}
