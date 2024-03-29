package com.habibInc.issueTracker.user;

import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class UserDto {

    private Long id;

    private String username;
    private String fullName;

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", userName:'" + username + '\'' +
                ", fullName:'" + fullName + '\'' +
                '}';
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return id.equals(userDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
