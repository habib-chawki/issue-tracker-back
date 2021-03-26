package com.habibInc.issueTracker.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class UserDto {
    private Long id;
    private String username;
    private String fullName;
}
