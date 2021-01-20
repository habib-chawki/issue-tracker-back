package com.habibInc.issueTracker.board;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
