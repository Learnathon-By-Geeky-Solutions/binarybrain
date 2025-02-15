package com.binaryBrain.classroom_microservice.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Set<String> roles;
    private String username;

    private String email;
}
