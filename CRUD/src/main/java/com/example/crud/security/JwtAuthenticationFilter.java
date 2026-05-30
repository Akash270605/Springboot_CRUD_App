// ============================================================
// FILE: JwtAuthenticationFilter.java
// This is a "Filter" — a piece of code that runs for EVERY
// incoming HTTP request before it reaches the Controller.
//
// Think of it as a security checkpoint at the airport:
// - It checks every passenger's ID (JWT token).
// - If the ID is valid, the passenger is allowed through.
// - If the ID is missing or fake, the passenger is turned away.
// ============================================================
package com.example.crud.security;

import com.example.crud.user.User;
import com.example.crud.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * ---------------------------------------------------------------
 * JWT AUTHENTICATION FILTER
 * ---------------------------------------------------------------
 * This class is a "Security Guard" that inspects every incoming
 * web request. It looks for a "Bearer Token" in the header to
 * identify who the user is.
 *
 * Key beginner concepts:
 * - @Component          → Marks this as a Spring-managed bean.
 * - OncePerRequestFilter → Runs once per request (not multiple times).
 * - FilterChain         → The chain of filters; calling doFilter()
 *                         passes the request to the next filter.
 * - SecurityContextHolder → Stores who is currently authenticated.
 * - ProblemDetail        → RFC 7807 standard for error responses.
 *
 * How it works step-by-step:
 *   1. Look for the "Authorization: Bearer <token>" header.
 *   2. If the header is missing → just continue the filter chain.
 *   3. Extract the username from the JWT token.
 *   4. Look up that user in the database.
 *   5. If the token is valid → set the user in Spring Security's context.
 *   6. If the token is invalid → return 401 Unauthorized.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;         // For parsing and validating JWT tokens.
    private final UserRepository userRepository; // For looking up users in the database.

    /**
     * Constructor injection.
     *
     * @param jwtService     Handles JWT parsing/validation.
     * @param userRepository Database access for users.
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ==========================================================
        // STEP 1: Look for the "Authorization" header.
        // The header should look like: "Authorization: Bearer eyJhbGci..."
        // ==========================================================
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // If the header is missing or doesn't start with "Bearer ",
        // just move to the next filter without authenticating anyone.
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // ==========================================================
            // STEP 2: Extract the token (remove "Bearer " prefix).
            // "Bearer " is 7 characters, so we take the rest of the string.
            // ==========================================================
            String token = header.substring(7);

            // ==========================================================
            // STEP 3: Extract the username from the token.
            // JwtService.extractUsername() validates the signature AND
            // checks that this is an ACCESS-type token (not a REFRESH token).
            // ==========================================================
            String username = jwtService.extractUsername(token, JwtService.TokenType.ACCESS);

            // ==========================================================
            // STEP 4: Check if the user is NOT already authenticated.
            // (If they are, we don't need to re-authenticate them.)
            // ==========================================================
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByUsername(username).orElse(null);

                // ==========================================================
                // STEP 5: Validate the token against the actual user.
                // This ensures the token belongs to this user and hasn't been
                // tampered with.
                // ==========================================================
                if (user != null && jwtService.isValidAccessToken(token, user)) {
                    // Create an Authentication object for Spring Security.
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,          // The "principal" (who is logged in).
                                    null,          // Credentials (not needed for JWT).
                                    user.getAuthorities() // Roles/permissions.
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Store the authenticated user in the SecurityContext.
                    // This makes them available to Controllers via @AuthenticationPrincipal.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            // Continue to the next filter (and eventually the Controller).
            filterChain.doFilter(request, response);

        } catch (RuntimeException ex) {
            // ==========================================================
            // STEP 6: If anything went wrong (expired token, bad signature),
            // return a 401 Unauthorized error immediately.
            // ==========================================================
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            problem.setTitle("Unauthorized");
            problem.setType(URI.create("https://api.example.com/problems/401"));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.getWriter().write("""
                    {"type":"%s","title":"%s","status":401,"detail":"%s"}
                    """.formatted(problem.getType(), problem.getTitle(), problem.getDetail()));
        }
    }
}