package com.habibInc.issueTracker.board;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.habibInc.issueTracker.column.ColumnDto;
import lombok.*;

import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class BoardDto {

    private Long id;

    private String name;
    private List<ColumnDto> columns;

    @JsonProperty("owner")
    private Long ownerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardDto boardDto = (BoardDto) o;
        return Objects.equals(id, boardDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
