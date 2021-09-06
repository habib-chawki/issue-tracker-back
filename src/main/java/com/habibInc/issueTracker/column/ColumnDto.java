package com.habibInc.issueTracker.column;

import com.habibInc.issueTracker.issue.IssueDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ColumnDto {

    private Long id;
    private String title;

    private List<IssueDto> issues;

    @Override
    public String toString() {
        return "{" +
                "id: " + id +
                ", title: '" + title + '\'' +
                ", issues: " + issues +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnDto columnDto = (ColumnDto) o;
        return id.equals(columnDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
