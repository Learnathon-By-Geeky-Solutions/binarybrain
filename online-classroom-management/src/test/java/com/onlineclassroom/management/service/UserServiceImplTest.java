package com.onlineclassroom.management.service;

import com.onlineclassroom.management.dto.UserDto;
import com.onlineclassroom.management.exception.user.ResourceNotFoundException;
import com.onlineclassroom.management.exception.user.UserAlreadyExistsException;
import com.onlineclassroom.management.service.impl.UserServiceImpl;
import com.onlineclassroom.management.model.Role;
import com.onlineclassroom.management.model.User;
import com.onlineclassroom.management.repository.RoleRepository;
import com.onlineclassroom.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserDto userDto;

    @BeforeEach
    void setUp(){
        userDto = new UserDto();
        userDto.setFirstName("Moinul");
        userDto.setLastName("Islam");
        userDto.setUsername("moinulislam");
        userDto.setCurrentInstitute("PUST");
        userDto.setCountry("Bangladesh");
        userDto.setGender("Male");
        userDto.setEmail("moinul@gmail.com");
        userDto.setProfilePicture("Add later");
        userDto.setPassword("password");
        userDto.setRoles(Set.of("STUDENT"));
    }

    @Test
    void registerUser_Success() throws UserAlreadyExistsException{

        Role userRole = new Role("STUDENT");
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.registerUser(userDto);

        assertNotNull(savedUser);
        assertEquals("moinulislam", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(userRole));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ThrowsException_WhenUsernameExists() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(userDto));
    }

    @Test
    void registerUser_ThrowsException_WhenEmailExists() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(userDto));
    }

    @Test
    void registerUser_ThrowsException_WhenRoleNotFound() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.registerUser(userDto));
    }

}