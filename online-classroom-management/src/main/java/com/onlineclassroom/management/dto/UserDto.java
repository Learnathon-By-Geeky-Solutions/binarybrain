package com.onlineclassroom.management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

/**
 * The {@code UserDTO} class is a Data Transfer Object used for user registration.
 * It contains the necessary fields for creating a new user.
 *
 * @author Md Moinul Islam Sourav
 * @since 2025-02-02
 */
@Data
public class UserDto {

    private Long id;

    @NotBlank(message = "First name is required!")
    @Size(min = 2, max = 10, message = "First name must be between 3 and 10 characters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Name must contain only letters")
    private String firstName;

    @NotBlank(message = "Last name is required!")
    @Size(min = 3, max = 15, message = "Last name must be between 3 and 15 characters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Name must contain only letters")
    private String lastName;

    private String currentInstitute;

    @NotBlank(message = "Country is required!")
    private String country;

    @NotBlank(message = "Gender is required!")
    private String gender;

    private Set<String> roles;

    private String profilePicture;

    @NotBlank(message = "Username is required!")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;

    @NotBlank(message = "Email is required!")
    @Email(message = "Invalid email format!")
    private String email;

    @NotBlank(message = "Password is required!")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}