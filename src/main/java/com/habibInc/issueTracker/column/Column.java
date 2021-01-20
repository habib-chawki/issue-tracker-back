package com.habibInc.issueTracker.column;

import lombok.*;

import javax.persistence.*;
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
