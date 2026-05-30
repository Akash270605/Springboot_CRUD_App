// ============================================================
// FILE: LoginRequest.java
// This is a "DTO" (Data Transfer Object) — a simple carrier
// that holds the login credentials coming FROM the client.
//
// It's a Java "record", which means the constructor, getters,
// equals(), hashCode(), and toString() are all auto-generated.
//
// The @NotBlank annotation ensures that the username and
// password cannot be empty or just whitespace.
// ============================================================
package com.example.crud.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * ---------------------------------------------------------------
 * LOGIN REQUEST DTO
 * ---------------------------------------------------------------
 * Represents the JSON body that a client sends when trying
 * to log in.
 *
 * Example JSON:
 * {
 *     "username": "alice",
 *     "password": "mySecretPassword"
 * }
 *
 * @param username The username of the account (required).
 * @param password The password (required).
 */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}