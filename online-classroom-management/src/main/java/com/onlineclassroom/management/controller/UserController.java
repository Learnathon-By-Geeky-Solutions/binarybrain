package com.onlineclassroom.management.controller;

import com.onlineclassroom.management.dto.UserDto;
import com.onlineclassroom.management.model.User;
import com.onlineclassroom.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * UserController class handles HTTP request related to user operation(s).
 *
 * @author Md Moinul Islam Sourav
 * @since 2025-02-02
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public  UserController (UserService userService){
        this.userService = userService;
    }

    /**
     * Registers a new user.
     *
     * @param userDto The {@code UserDTO} object containing user details. {@code @Valid} checks the data in userDto meets the validation constrains (e.g. @NotBlank, @Size, @Email, etc.).
     * @param result If validation fails, {@code BindingResult} captures validation errors and allows to handle validation error manually.
     * @return The {@code ?} (wildcard) can return different types of responses.
     *                If registration succeeeds, it returns {@code User}.
     *                If validation fails, it returns {@code List<String>} (error messages).
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDto userDto, BindingResult result){

        if(result.hasErrors()){
            List<String> errorList = result.getAllErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(errorList);
        }

        User createdUser = userService.registerUser(userDto);
        return new  ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
