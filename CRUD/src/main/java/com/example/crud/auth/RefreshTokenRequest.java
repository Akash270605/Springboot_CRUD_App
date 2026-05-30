// ============================================================
// FILE: RefreshTokenRequest.java
// This is a simple DTO that holds the Refresh Token sent by
// the client when they want to get a new Access Token without
// logging in again.
//
// A Refresh Token has a longer lifespan (days) and is used to
// obtain a new short-lived Access Token (minutes) once the
// old one expires.
// ============================================================
package com.example.crud.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * ---------------------------------------------------------------
 * REFRESH TOKEN REQUEST DTO
 * ---------------------------------------------------------------
 * Represents the JSON body sent to the "/api/auth/refresh" endpoint.
 *
 * Example JSON:
 * {
 *     "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
 * }
 *
 * @param refreshToken The long-lived JWT token (required).
 */
public record RefreshTokenRequest(@NotBlank String refreshToken) {
}