package com.habibInc.issueTracker.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class UserDto {
    private Long id;
    private String userName;
    private String fullName;
}
