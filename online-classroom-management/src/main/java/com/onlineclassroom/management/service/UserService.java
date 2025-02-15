package com.onlineclassroom.management.service;

import com.onlineclassroom.management.dto.UserDto;
import com.onlineclassroom.management.model.User;

import java.util.Optional;

/**
 * @author Md Moinul Islam Sourav
 * @since 2025-02-02
 */

public interface UserService {
    User registerUser(UserDto userDto);
    Optional<User> getUserProfile(String jwt);
}