package com.binarybrain.user.controller;

import com.binarybrain.exception.AlreadyExistsException;
import com.binarybrain.exception.InvalidTokenException;
import com.binarybrain.exception.global.GlobalExceptionHandler;
import com.binarybrain.user.dto.request.RefreshTokenRequest;
import com.binarybrain.user.repository.RefreshTokenRepository;
import com.binarybrain.user.repository.UserRepository;
import com.binarybrain.user.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.binarybrain.user.dto.UserDto;
import com.binarybrain.user.dto.request.AuthRequest;
import com.binarybrain.user.mapper.UserMapper;
import com.binarybrain.user.model.RefreshToken;
import com.binarybrain.user.model.User;
import com.binarybrain.user.security.JwtUtil;
import com.binarybrain.user.service.CustomUserDetailsService;
import com.binarybrain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.authentication.BadCredentialsException;

@SpringBootTest
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
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
    private UserRepository userRepository;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private RefreshTokenService refreshTokenService;
    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private ObjectMapper objectMapper;

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
        UserMapper.userToUserDtoMapper(createdUser); //Just for coverage
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
                .thenThrow(new AlreadyExistsException("Error! Username is already exists: " + userDto.getUsername()));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Error! Username is already exists: " + userDto.getUsername()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.details").exists());

        verify(userService, times(1)).registerUser(any(UserDto.class));
    }

    @Test
    void testRegisterUser_DuplicateEmail() throws Exception {
        when(userService.registerUser(any(UserDto.class)))
                .thenThrow(new AlreadyExistsException("Error! Email is already exist: " + userDto.getEmail()));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Error! Email is already exist: " + userDto.getEmail()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.details").exists());

        verify(userService, times(1)).registerUser(any(UserDto.class));
    }

    @Test
    void testLoginUser_Success() throws Exception {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("moinulislam")
                .password("password")
                .roles("STUDENT")
                .build();

        User user = new User();
        user.setUsername("moinulislam");

        when(userRepository.findByUsername("moinulislam")).thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("moinulislam")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("refresh-token");
        when(refreshTokenService.createRefreshToken(any())).thenReturn(mockRefreshToken);

        when(authenticationManager.authenticate(any())).thenReturn(null);

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
                .thenThrow(new BadCredentialsException("Incorrect username or password!"));

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Incorrect username or password!"));

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        User user = new User();
        user.setUsername("moinulislam");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("valid-refresh-token");
        mockRefreshToken.setUser(user);
        mockRefreshToken.setExpiryDate(Instant.now().plusSeconds(3600)); // 1 hour from now

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("moinulislam")
                .password("password")
                .roles("STUDENT")
                .build();

        when(refreshTokenService.findByToken("valid-refresh-token")).thenReturn(Optional.of(mockRefreshToken));
        when(userDetailsService.loadUserByUsername("moinulislam")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("new-access-token");

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("valid-refresh-token");

        mockMvc.perform(post("/api/user/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("new-access-token"))
                .andExpect(jsonPath("$.refreshToken").value("valid-refresh-token"));

        verify(refreshTokenService, times(1)).findByToken("valid-refresh-token");
        verify(refreshTokenService, times(1)).verifyExpiration(mockRefreshToken);
        verify(userDetailsService, times(1)).loadUserByUsername("moinulislam");
        verify(jwtUtil, times(1)).generateToken(userDetails);
    }
    @Test
    void testRefreshToken_ExpiredToken() throws Exception {
        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("expired-refresh-token");
        mockRefreshToken.setExpiryDate(Instant.now().minusSeconds(3600)); // Expired 1 hour ago

        when(refreshTokenService.findByToken("expired-refresh-token")).thenReturn(Optional.of(mockRefreshToken));
        doThrow(new InvalidTokenException("Refresh token has expired. Please log in again."))
                .when(refreshTokenService).verifyExpiration(mockRefreshToken);

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("expired-refresh-token");

        mockMvc.perform(post("/api/user/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Refresh token has expired. Please log in again."));

        verify(refreshTokenService, times(1)).findByToken("expired-refresh-token");
        verify(refreshTokenService, times(1)).verifyExpiration(mockRefreshToken);
    }
}
