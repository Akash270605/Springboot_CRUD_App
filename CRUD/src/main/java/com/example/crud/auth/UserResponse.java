// ============================================================
// FILE: UserResponse.java
// This is a "DTO" that represents the user information sent
// back to the client when they ask "Who am I?" at the
// /api/auth/me endpoint.
//
// It deliberately does NOT include the password (for security).
// ============================================================
package com.example.crud.auth;

import com.example.crud.user.Role;
import java.time.Instant;

/**
 * ---------------------------------------------------------------
 * USER RESPONSE DTO
 * ---------------------------------------------------------------
 * Represents the JSON returned to the client when they
 * request their own profile information.
 *
 * Example JSON:
 * {
 *     "id": 1,
 *     "username": "alice",
 *     "email": "alice@example.com",
 *     "role": "ROLE_USER",
 *     "createdAt": "2026-05-30T10:00:00Z"
 * }
 *
 * @param id        The unique user ID.
 * @param username  The login username.
 * @param email     The user's email address.
 * @param role      The user's role (USER or ADMIN).
 * @param createdAt When the account was created.
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        Instant createdAt
) {
}