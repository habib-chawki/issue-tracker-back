package com.habibInc.issueTracker.board;

import com.habibInc.issueTracker.column.Column;
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
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany
    private List<Column> columns;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id) &&
                Objects.equals(name, board.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
