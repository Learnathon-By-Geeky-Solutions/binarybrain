package com.binarybrain.user.service;

import com.binarybrain.user.model.Role;
import com.binarybrain.user.model.User;
import com.binarybrain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void loadUserByUsername_Success() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRoles(Set.of(new Role("ROLE_STUDENT")));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_STUDENT", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("testuser"));
    }
}
