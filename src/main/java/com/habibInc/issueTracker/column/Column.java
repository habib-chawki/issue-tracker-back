package com.habibInc.issueTracker.column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.habibInc.issueTracker.board.Board;
import com.habibInc.issueTracker.issue.Issue;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder

@Table(name = "`column`")
@JsonIgnoreProperties({"board"})
public class Column {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @OneToMany(mappedBy = "column")
    List<Issue> issues = new ArrayList<>();

    @Override
    public String toString() {
        return "{" +
                "id: " + id +
                ", title: '" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return id.equals(column.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
