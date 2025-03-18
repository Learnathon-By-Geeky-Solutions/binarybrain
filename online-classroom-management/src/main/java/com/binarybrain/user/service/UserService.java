package com.binarybrain.user.service;

import com.binarybrain.user.dto.UserDto;
import com.binarybrain.user.model.User;

import java.util.Optional;

/**
 * @author Md Moinul Islam Sourav
 * @since 2025-02-02
 */

public interface UserService {
    User registerUser(UserDto userDto);
    Optional<User> getUserProfile(String username);
    User getUserProfileById(Long id, String username);
}