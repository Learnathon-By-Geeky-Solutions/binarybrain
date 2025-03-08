package com.onlineclassroom.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlineclassroom.management.dto.UserDto;
import com.onlineclassroom.management.dto.request.AuthRequest;
import com.onlineclassroom.management.exception.user.UserAlreadyExistsException;
import com.onlineclassroom.management.mapper.UserMapper;
import com.onlineclassroom.management.model.RefreshToken;
import com.onlineclassroom.management.model.User;
import com.onlineclassroom.management.security.JwtUtil;
import com.onlineclassroom.management.service.CustomUserDetailsService;
import com.onlineclassroom.management.service.UserService;
import com.onlineclassroom.management.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.authentication.BadCredentialsException;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private UserDto userDto;
    private User createdUser;
    private AuthRequest authRequest;

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

        createdUser = UserMapper.userDtoToUserMapper(userDto);
        createdUser.setId(1L);

        authRequest = new AuthRequest();
        authRequest.setUsername("moinulislam");
        authRequest.setPassword("password");
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        when(userService.registerUser(any(UserDto.class))).thenReturn(createdUser);

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.firstName").value(createdUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(createdUser.getLastName()))
                .andExpect(jsonPath("$.username").value(createdUser.getUsername()))
                .andExpect(jsonPath("$.currentInstitute").value(createdUser.getCurrentInstitute()))
                .andExpect(jsonPath("$.email").value(createdUser.getEmail()))
                .andExpect(jsonPath("$.profilePicture").value(createdUser.getProfilePicture()))
                .andExpect(jsonPath("$.country").value(createdUser.getCountry()))
                .andExpect(jsonPath("$.roles").value(createdUser.getRoles()))
                .andExpect(jsonPath("$.password").value(createdUser.getPassword()))
                .andExpect(jsonPath("$.gender").value(createdUser.getGender()));

        verify(userService, times(1)).registerUser(any(UserDto.class));
    }

    @Test
    void testRegisterUser_ValidationErrors() throws Exception {
        UserDto invalidUserDto = new UserDto();

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void testRegisterUser_DuplicateUsername() throws Exception {
        when(userService.registerUser(any(UserDto.class)))
                .thenThrow(new UserAlreadyExistsException("Username already exists!"));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists!"));

        verify(userService, times(1)).registerUser(any(UserDto.class));
    }

    @Test
    void testRegisterUser_DuplicateEmail() throws Exception {
        when(userService.registerUser(any(UserDto.class)))
                .thenThrow(new UserAlreadyExistsException("Email already exists!"));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists!"));

        verify(userService, times(1)).registerUser(any(UserDto.class));
    }

    @Test
    void testLoginUser_Success() throws Exception {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("moinulislam")
                .password("password")
                .roles("STUDENT")
                .build();

        when(userDetailsService.loadUserByUsername("moinulislam")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("refresh-token");
        when(refreshTokenService.createRefreshToken(any())).thenReturn(mockRefreshToken);

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(userDetailsService, times(1)).loadUserByUsername("moinulislam");
        verify(jwtUtil, times(1)).generateToken(userDetails);
        verify(refreshTokenService, times(1)).createRefreshToken(any());
    }

    @Test
    void testLoginUser_InvalidCredentials() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Incorrect username or password!"));

        verify(authenticationManager, times(1)).authenticate(any());
    }
}
