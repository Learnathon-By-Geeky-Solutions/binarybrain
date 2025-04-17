package com.binarybrain.user.controller;

import com.binarybrain.exception.AlreadyExistsException;
import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.user.dto.request.RefreshTokenRequest;
import com.binarybrain.user.repository.UserRepository;
import com.binarybrain.user.service.RefreshTokenService;
import com.binarybrain.user.service.UserImageService;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserImageService imageService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private RefreshTokenService refreshTokenService;

    private UserDto userDto;
    private User createdUser;
    private AuthRequest authRequest;
    @MockitoBean
    private UserRepository userRepository;

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

        UserMapper.userToUserDtoMapper(createdUser); //For coverage

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
                .thenThrow(new AlreadyExistsException("Username already exists!"));

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDto)));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isConflict());

    }

    @Test
    void testRegisterUser_DuplicateEmail() throws Exception {
        when(userService.registerUser(any(UserDto.class)))
                .thenThrow(new AlreadyExistsException("Email already exists!"));

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDto)))
                .andExpect(status().isConflict());
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
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(createdUser));
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
                .andExpect(status().isForbidden());

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("valid-refresh-token");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("valid-refresh-token");
        refreshToken.setUser(createdUser);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("moinulislam")
                .password("password")
                .roles("STUDENT")
                .build();

        when(refreshTokenService.findByToken("valid-refresh-token"))
                .thenReturn(Optional.of(refreshToken));
        when(userDetailsService.loadUserByUsername("moinulislam"))
                .thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails))
                .thenReturn("new-jwt-token");

        mockMvc.perform(post("/api/user/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("new-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("valid-refresh-token"));

        verify(refreshTokenService, times(1)).findByToken("valid-refresh-token");
        verify(refreshTokenService, times(1)).verifyExpiration(refreshToken);
        verify(userDetailsService, times(1)).loadUserByUsername("moinulislam");
        verify(jwtUtil, times(1)).generateToken(userDetails);
    }

    @Test
    void testGetUserProfileById_Success() throws Exception {
        when(userService.getUserProfileById(1L, "moinulislam"))
                .thenReturn(createdUser);

        mockMvc.perform(get("/api/user/profile/1")
                        .header("X-User-Username", "moinulislam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.username").value(createdUser.getUsername()));

        verify(userService, times(1)).getUserProfileById(1L, "moinulislam");
    }

    @Test
    void testGetUserProfileById_NotFound() throws Exception {
        when(userService.getUserProfileById(999L, "moinulislam"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/user/profile/999")
                        .header("X-User-Username", "moinulislam"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserProfileById(999L, "moinulislam");
    }

    @Test
    void testUploadPhoto() throws Exception {
        Long userId = 1L;
        String username = "testuser";
        String photoUrl = "http://localhost:8080/image/photo123.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy image content".getBytes());

        when(imageService.uploadPhoto(eq(userId), any(MultipartFile.class), eq(username)))
                .thenReturn(photoUrl);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/photo")
                        .file(file)
                        .param("id", userId.toString())
                        .header("X-User-Username", username))
                .andExpect(status().isOk())
                .andExpect(content().string(photoUrl));
    }

    @Test
    void testGetPhoto() throws Exception {
        String filename = "photo123.jpg";
        byte[] photoContent = "dummy image content".getBytes();

        when(imageService.getPhoto(filename)).thenReturn(photoContent);

        mockMvc.perform(get("/api/user/photo/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().bytes(photoContent));
    }

    @Test
    void testSearchByImage() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy content".getBytes());

        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");

        when(imageService.searchUsersByImage(any(MultipartFile[].class)))
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/user/search-by-image")
                        .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }


}
