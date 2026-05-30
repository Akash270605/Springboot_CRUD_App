// ============================================================
// FILE: AuthService.java
// This is a "Service" class that handles all authentication
// logic: registering new users, logging in, and refreshing tokens.
//
// It works closely with:
//   - UserRepository  → To find/save users in the database.
//   - JwtService      → To create and validate JWT tokens.
//   - PasswordEncoder → To hash passwords (for security).
//   - AuthenticationManager → To verify login credentials.
// ============================================================
package com.example.crud.auth;

import com.example.crud.exception.BadRequestException;
import com.example.crud.security.JwtService;
import com.example.crud.user.Role;
import com.example.crud.user.User;
import com.example.crud.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ---------------------------------------------------------------
 * AUTH SERVICE
 * ---------------------------------------------------------------
 * This service handles all logic related to User security:
 * signing up, logging in, and keeping sessions alive with tokens.
 *
 * Key beginner concepts:
 * - @Service         → Marks this as a Spring-managed service bean.
 * - @Transactional   → Database operations are wrapped in a transaction.
 * - PasswordEncoder  → BCrypt hashing — we NEVER store plain-text passwords.
 * - AuthenticationManager → Spring's built-in login verifier.
 */
@Service
public class AuthService {

    // Dependencies injected via constructor:
    private final UserRepository userRepository;         // Database access for users.
    private final PasswordEncoder passwordEncoder;       // For hashing/checking passwords.
    private final JwtService jwtService;                 // For generating JWT tokens.
    private final AuthenticationManager authenticationManager; // Spring Security's login handler.

    /**
     * Constructor Injection: Spring provides these tools automatically when the app starts.
     *
     * @param userRepository        Database operations for users.
     * @param passwordEncoder       BCrypt password hasher.
     * @param jwtService            JWT token generator/parser.
     * @param authenticationManager Spring's login authenticator.
     */
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Logic for creating a new user account.
     *
     * Steps:
     *   1. Check if username or email is already taken.
     *   2. Create the User object and hash the password.
     *   3. Save the user to the database.
     *   4. Return JWT tokens so the user is automatically logged in.
     *
     * @Transactional ensures that if any step fails, the database
     * is not left in a half-updated state.
     *
     * @param request The registration data (username, email, password).
     * @return AuthResponse containing access and refresh tokens.
     * @throws BadRequestException if username or email is already taken.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Check if the username or email is already taken.
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        // 2. Create the user object and encrypt the password (never store plain text!).
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ROLE_USER); // New signups are regular users by default, not admins.
        userRepository.save(user);    // Save to database.

        // 3. Automatically log them in by returning fresh JWT tokens.
        return jwtService.tokensFor(user);
    }

    /**
     * Validates login credentials and returns JWT tokens.
     *
     * The AuthenticationManager handles the heavy lifting of
     * comparing the provided password with the hashed one
     * stored in the database.
     *
     * @param request The login data (username and password).
     * @return AuthResponse containing access and refresh tokens.
     * @throws org.springframework.security.authentication.BadCredentialsException
     *         if the username or password is wrong.
     */
    public AuthResponse login(LoginRequest request) {
        // Ask Spring Security to verify the username + password.
        // If the credentials are wrong, an exception is thrown automatically.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        // If we reach here, authentication was successful.
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        return jwtService.tokensFor(user);
    }

    /**
     * Takes an old Refresh Token and gives back a new set of tokens
     * (Access + Refresh). This lets users stay logged in without
     * re-entering their password.
     *
     * @param request Contains the old refresh token.
     * @return AuthResponse with a fresh access token and refresh token.
     * @throws BadRequestException if the refresh token is invalid or expired.
     */
    public AuthResponse refresh(RefreshTokenRequest request) {
        // 1. Extract the username from the refresh token.
        String username = jwtService.extractUsername(request.refreshToken(), JwtService.TokenType.REFRESH);
        // 2. Look up the user in the database.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        // 3. Generate and return new tokens.
        return jwtService.tokensFor(user);
    }
}