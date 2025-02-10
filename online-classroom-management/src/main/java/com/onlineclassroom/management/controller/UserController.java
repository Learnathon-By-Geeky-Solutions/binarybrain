package com.onlineclassroom.management.controller;

import com.onlineclassroom.management.dto.UserDto;
import com.onlineclassroom.management.dto.request.AuthRequest;
import com.onlineclassroom.management.dto.request.RefreshTokenRequest;
import com.onlineclassroom.management.dto.response.AuthResponse;
import com.onlineclassroom.management.model.RefreshToken;
import com.onlineclassroom.management.model.User;
import com.onlineclassroom.management.repository.UserRepository;
import com.onlineclassroom.management.service.CustomUserDetailsService;
import com.onlineclassroom.management.service.RefreshTokenService;
import com.onlineclassroom.management.service.UserService;
import com.onlineclassroom.management.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          CustomUserDetailsService userDetailsService,
                          UserRepository userRepository,
                          RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
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
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorList);
        }

        User createdUser = userService.registerUser(userDto);
        return new  ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param authRequest The {@code AuthRequest} containing username and password.
     * @return The {@code AuthResponse} containing the JWT token if authentication is successful.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authRequest.getUsername()));
        String jwtToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new AuthResponse(jwtToken, refreshToken.getToken()));
    }

    /**
     * This endpoint allows a user to obtain a new access token using a valid refresh token.
     * @param request A {@code RefreshTokenRequest} containing the refresh token in the request body.
     * @return A {@link ResponseEntity} containing a new access token and the same refresh token.
     *  *         If the refresh token is invalid or expired, an exception is thrown.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found!"));

        refreshTokenService.verifyExpiration(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUser().getUsername());
        String newAccessToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken.getToken()));
    }
}
