package com.binaryBrain.taskSubmission.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Set<RoleDto> roles;
    private String username;
}
