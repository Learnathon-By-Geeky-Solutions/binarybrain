package com.binarybrain.user.service;

import com.binarybrain.exception.AlreadyExistsException;
import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.user.dto.UserDto;
import com.binarybrain.user.mapper.UserMapper;
import com.binarybrain.user.service.impl.UserServiceImpl;
import com.binarybrain.user.model.Role;
import com.binarybrain.user.model.User;
import com.binarybrain.user.repository.RoleRepository;
import com.binarybrain.user.repository.UserRepository;
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
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserDto userDto;
    private User testUser;

    @BeforeEach
    void setUp(){
        userDto = new UserDto();
        userDto.setId(1L);
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

        testUser = UserMapper.userDtoToUserMapper(userDto);
    }

    @Test
    void registerUser_Success() throws AlreadyExistsException {

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

        assertThrows(AlreadyExistsException.class, () -> userService.registerUser(userDto));
    }

    @Test
    void registerUser_ThrowsException_WhenEmailExists() {
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistsException.class, () -> userService.registerUser(userDto));
    }

    @Test
    void registerUser_ThrowsException_WhenRoleNotFound() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.registerUser(userDto));
    }

    @Test
    void getUserProfile_Success() {
        String username = "moinulislam";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserProfile(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUserProfile_ReturnsEmptyOptional_WhenUserNotFound() {
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserProfile(username);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUserProfileById_Success() {
        Long userId = 1L;
        String username = "moinulislam";
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User result = userService.getUserProfileById(userId, username);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserProfileById_ThrowsException_WhenUserNotFound() {
        Long userId = 99L;
        String username = "moinulislam";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserProfileById(userId, username));
        verify(userRepository, times(1)).findById(userId);
    }
}