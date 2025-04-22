package com.binarybrain.user.controller;

import com.binarybrain.exception.ErrorDetails;
import com.binarybrain.exception.ResourceNotFoundException;
import com.binarybrain.user.dto.UserDto;
import com.binarybrain.user.dto.request.AuthRequest;
import com.binarybrain.user.dto.request.RefreshTokenRequest;
import com.binarybrain.user.dto.response.AuthResponse;
import com.binarybrain.user.model.RefreshToken;
import com.binarybrain.user.model.User;
import com.binarybrain.user.repository.UserRepository;
import com.binarybrain.user.service.CustomUserDetailsService;
import com.binarybrain.user.service.RefreshTokenService;
import com.binarybrain.user.service.UserService;
import com.binarybrain.user.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

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
    @Operation(
            summary = "Create a new user",
            description = "To Register a new user, you should provide unique username, email and a valid ROLE.\n Acceptable user ROLE list: [ADMIN, TEACHER, STUDENT].",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., missing fields)"),
                    @ApiResponse(responseCode = "404", description = "User ROLE not found! Please try with \"ADMIN\", \"TEACHER\" or \"STUDENT\" ",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))),
                    @ApiResponse(responseCode = "409", description = "Username or Email already exist!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            }
    )
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

    @Operation(
            summary = "User Login",
            description = "Authenticates a user with username and password. Returns JWT and Refresh Token on success.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User login successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., missing fields)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - bad credentials!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            }
    )
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
    @Operation(
            summary = "New JWT by Refresh Token",
            description = "JWT expiration time is set for 1 day while the Refresh token for 3 days. Generates a new JWT access token using a valid refresh token instead of further login.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "New jwt token and refresh token generated successfully"),
                    @ApiResponse(responseCode = "404", description = "Refresh token not found!"),
                    @ApiResponse(responseCode = "403", description = "Refresh token expired or invalid",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found!"));

        refreshTokenService.verifyExpiration(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUser().getUsername());
        String newAccessToken = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken.getToken()));
    }

    @Operation(
            summary = "Get profile of the authenticated user from JWT",
            description = "Returns the profile of the user extracted from the Bearer token. This request has no RequestBody, just add JWT as AUTHORIZATION header.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired token!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/profile")
    public ResponseEntity<Optional<User>> getUserProfile(@Parameter(hidden = true) @RequestHeader("X-User-Username") String username){
        Optional<User> user= userService.getUserProfile(username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Operation(
            summary = "Get profile by user ID",
            description = "Returns the profile of the user identified by userId.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired token!",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class)))
            },
            security = @SecurityRequirement(name = "bearerToken")
    )
    @GetMapping("/profile/{id}")
    public ResponseEntity<User> getUserProfileById(@PathVariable Long id,
                                                   @Parameter(hidden = true)
                                                   @RequestHeader("X-User-Username") String username){
        User user = userService.getUserProfileById(id, username);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}