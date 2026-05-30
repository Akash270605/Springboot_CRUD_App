// ============================================================
// FILE: AuthController.java
// This "Controller" handles all authentication-related HTTP
// endpoints: registration, login, token refresh, and viewing
// the current user's profile.
//
// All endpoints start with "/api/auth".
// ============================================================
package com.example.crud.auth;

import com.example.crud.user.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * ---------------------------------------------------------------
 * AUTH CONTROLLER
 * ---------------------------------------------------------------
 * Handles public endpoints for:
 *   - POST /api/auth/register   → Create a new account.
 *   - POST /api/auth/login      → Log in and get tokens.
 *   - POST /api/auth/refresh    → Get new tokens using a refresh token.
 *   - GET  /api/auth/me         → Get the currently logged-in user's info.
 *
 * The first three are PUBLIC (no token required).
 * The last one (/me) requires a valid JWT token.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Dependencies injected via constructor:
    private final AuthService authService;   // Business logic for auth.
    private final UserMapper userMapper;     // Converts User → UserResponse.

    /**
     * Constructor injection — Spring provides the dependencies.
     *
     * @param authService The service handling auth operations.
     * @param userMapper  The mapper for converting User entities to DTOs.
     */
    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    /**
     * POST /api/auth/register
     * Creates a new user account.
     *
     * HTTP 201 (Created) is returned on success.
     *
     * @param request The registration details (username, email, password).
     * @return AuthResponse with JWT tokens (auto-login after registration).
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * POST /api/auth/login
     * Authenticates an existing user and returns JWT tokens.
     *
     * @param request The login credentials (username, password).
     * @return AuthResponse with JWT tokens.
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * POST /api/auth/refresh
     * Uses a valid refresh token to get a new access token.
     * This allows users to stay logged in without re-entering
     * their password.
     *
     * @param request Contains the old refresh token.
     * @return AuthResponse with fresh JWT tokens.
     */
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    /**
     * GET /api/auth/me
     * Returns information about the currently logged-in user.
     *
     * @AuthenticationPrincipal automatically extracts the User
     * object from the JWT token (set up by JwtAuthenticationFilter).
     *
     * @param user The currently authenticated user (injected by Spring Security).
     * @return UserResponse with the user's profile details.
     */
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return userMapper.toResponse(user);
    }
}