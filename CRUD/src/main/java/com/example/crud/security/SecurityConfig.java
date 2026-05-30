// ============================================================
// FILE: SecurityConfig.java
// This is a "Configuration" class that sets up Spring Security
// for the entire application.
//
// Think of it as the "Security Guard's instruction manual":
// - It decides which URLs are public (anyone can visit).
// - It decides which URLs require a login.
// - It decides which URLs require ADMIN privileges.
// - It tells Spring how to look up users and verify passwords.
// ============================================================
package com.example.crud.security;

import com.example.crud.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * ---------------------------------------------------------------
 * SECURITY CONFIGURATION
 * ---------------------------------------------------------------
 * The main configuration for application security.
 * Defines which URLs are public and which require a login or
 * specific roles.
 *
 * Key beginner concepts:
 * - @Configuration           → This is a Spring configuration class.
 * - @EnableWebSecurity       → Turns on Spring Security features.
 * - @EnableMethodSecurity    → Allows using @PreAuthorize on methods.
 * - SecurityFilterChain      → A chain of security rules applied to HTTP requests.
 * - CSRF                     → A web attack protection; disabled here because
 *                              we use JWT tokens, not cookies.
 * - STATELESS sessions       → No server-side session; each request is independent.
 * - BCryptPasswordEncoder    → Strong password hashing algorithm.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    // The JWT filter that validates tokens on every request.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor injection.
     *
     * @param jwtAuthenticationFilter The filter that checks JWT tokens.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Defines the "Chain" of security steps for HTTP requests.
     *
     * This is the main method that configures:
     *   - Which URLs are public (no token needed).
     *   - Which URLs require authentication.
     *   - Which URLs require the ADMIN role.
     *   - How to handle errors (401 Unauthorized, 403 Forbidden).
     *
     * @param http                   The HTTP security configurer.
     * @param authenticationProvider The provider that checks credentials.
     * @return A fully configured SecurityFilterChain.
     * @throws Exception if configuration fails.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider)
            throws Exception {
        http
            // Disable CSRF because we use stateless JWT tokens (not cookies).
            .csrf(csrf -> csrf.disable())

            // Tell Spring NOT to create HTTP sessions (stateless API).
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Set up the authentication provider (username/password checker).
            .authenticationProvider(authenticationProvider)

            // ==========================================================
            // ERROR HANDLING
            // Custom error messages for authentication/authorization failures.
            // ==========================================================
            .exceptionHandling(exceptions -> exceptions
                    // 401: User is not logged in (no token or invalid token).
                    .authenticationEntryPoint((request, response, authException) ->
                            writeProblem(response, HttpServletResponse.SC_UNAUTHORIZED,
                                    "Unauthorized", "Authentication is required"))
                    // 403: User is logged in but doesn't have the right role.
                    .accessDeniedHandler((request, response, accessDeniedException) ->
                            writeProblem(response, HttpServletResponse.SC_FORBIDDEN,
                                    "Forbidden", "Access is denied"))
            )

            // ==========================================================
            // URL AUTHORIZATION RULES
            // (Order matters — more specific rules first.)
            // ==========================================================
            .authorizeHttpRequests(auth -> auth
                    // Publicly accessible paths (no token needed):
                    .requestMatchers("/api/auth/register", "/api/auth/login",
                            "/api/auth/refresh").permitAll()
                    .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                            "/v3/api-docs/**", "/h2-console/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()

                    // ADMIN-only paths:
                    .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasRole("ADMIN")

                    // Everything else needs a valid JWT token:
                    .anyRequest().authenticated()
            )

            // Allow the H2 database console to display in a browser frame.
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // Add our custom JWT filter BEFORE Spring's default authentication filter.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Helper method that writes a JSON error response to the HTTP response.
     * Uses the RFC 7807 Problem Details format.
     *
     * @param response The HTTP response object.
     * @param status   The HTTP status code (e.g., 401, 403).
     * @param title    A short title for the error.
     * @param detail   A detailed description of the error.
     * @throws java.io.IOException if writing to the response fails.
     */
    private void writeProblem(HttpServletResponse response, int status,
                               String title, String detail) throws java.io.IOException {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status), detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://api.example.com/problems/" + status));
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.getWriter().write("""
                {"type":"%s","title":"%s","status":%d,"detail":"%s"}
                """.formatted(problem.getType(), problem.getTitle(), status, problem.getDetail()));
    }

    /**
     * Tells Spring Security how to find a user in our database.
     *
     * This is called during login when the user provides a username.
     * We look up the user in our UserRepository and return the User object
     * (which implements UserDetails).
     *
     * @param userRepository Our database repository for users.
     * @return A UserDetailsService that loads users from our database.
     */
    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * The bridge between UserDetailsService and the PasswordEncoder.
     *
     * This provider:
     * 1. Takes the username/password from the login request.
     * 2. Loads the user from the database (via UserDetailsService).
     * 3. Compares the provided password with the stored hashed password
     *    (using PasswordEncoder).
     *
     * @param userDetailsService Service to load users from the database.
     * @param passwordEncoder    The password hashing algorithm.
     * @return An AuthenticationProvider ready to verify credentials.
     */
    @Bean
    AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Standard utility for handling authentication requests.
     * Used by AuthService.login() to verify credentials.
     *
     * @param configuration Spring's authentication configuration.
     * @return The AuthenticationManager.
     * @throws Exception if the manager cannot be created.
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Defines the algorithm used to hash passwords.
     *
     * BCrypt is the industry standard for password hashing.
     * It automatically adds a random "salt" to each password
     * and is intentionally slow to make brute-force attacks harder.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}