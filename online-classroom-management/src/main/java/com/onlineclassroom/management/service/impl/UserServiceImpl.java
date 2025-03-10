package com.onlineclassroom.management.service.impl;

import com.onlineclassroom.management.dto.UserDto;
import com.onlineclassroom.management.exception.user.ResourceNotFoundException;
import com.onlineclassroom.management.exception.user.UserAlreadyExistsException;
import com.onlineclassroom.management.mapper.UserMapper;
import com.onlineclassroom.management.model.Role;
import com.onlineclassroom.management.model.User;
import com.onlineclassroom.management.repository.RoleRepository;
import com.onlineclassroom.management.repository.UserRepository;
import com.onlineclassroom.management.security.JwtUtil;
import com.onlineclassroom.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
/**
 * Implementation of the UserService interface for user registration.
 * This class handles user validation, password encryption, and role assignment.
 *
 * @author Md Moinul Islam SOurav
 * @since 2025-02-02
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user by validating username and email, encrypting the password,
     *      and assigning roles (ADMIN, TEACHER, STUDENT).
     *
     * @param userDto The UserDTO object containing user registration details.
     * @return The newly created user entity.
     * @throws UserAlreadyExistsException If the username or email already exists.
     * @throws ResourceNotFoundException If a specified role is not found.
     */
    @Transactional
    @Override
    public User registerUser(UserDto userDto) {
        String username = userDto.getUsername();
        String email = userDto.getEmail();

        if(userRepository.findByUsername(username).isPresent()){
            throw new UserAlreadyExistsException("Error! Username is already exists: " + username);
        }
        if(userRepository.findByEmail(email).isPresent()){
            throw new UserAlreadyExistsException("Error! Email is already exist: " + email);
        }

        User user = UserMapper.userDtoToUserMapper(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if(userDto.getRoles() != null){
            Set<Role> roles = new HashSet<>();
            for(String roleName : userDto.getRoles()){
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserProfile(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserProfileById(Long id, String username) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}