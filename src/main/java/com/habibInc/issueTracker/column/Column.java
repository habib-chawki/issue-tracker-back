package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.board.Board;
import com.habibInc.issueTracker.issue.Issue;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    @ManyToOne
    private Board board;

    @OneToMany(mappedBy = "column")
    List<Issue> issues;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return Objects.equals(id, column.id) &&
                Objects.equals(title, column.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
