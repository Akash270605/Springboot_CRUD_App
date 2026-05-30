// ============================================================
// FILE: RegisterRequest.java
// This is a "DTO" (Data Transfer Object) that holds the data
// coming FROM the client when a new user wants to sign up.
//
// The validation annotations ensure that:
//   - username is between 3 and 50 characters.
//   - email is a valid email format.
//   - password is at least 8 characters long.
// ============================================================
package com.example.crud.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ---------------------------------------------------------------
 * REGISTER REQUEST DTO
 * ---------------------------------------------------------------
 * Represents the JSON body sent by a client to create a new
 * user account.
 *
 * Example JSON:
 * {
 *     "username": "charlie",
 *     "email": "charlie@example.com",
 *     "password": "SecurePass123"
 * }
 *
 * @param username  The desired username (3–50 characters, required).
 * @param email     The email address (valid format required, max 120 chars).
 * @param password  The password (8–80 characters, required).
 */
public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(min = 8, max = 80) String password
) {
}