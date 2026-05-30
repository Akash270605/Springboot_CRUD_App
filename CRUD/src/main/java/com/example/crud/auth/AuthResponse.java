// ============================================================
// FILE: AuthResponse.java
// This is a "DTO" that is sent BACK TO the client after a
// successful login, registration, or token refresh.
//
// It contains the JWT tokens the client needs to authenticate
// future requests.
// ============================================================
package com.example.crud.auth;

/**
 * ---------------------------------------------------------------
 * AUTH RESPONSE DTO
 * ---------------------------------------------------------------
 * Represents the JSON returned to the client after successful
 * authentication.
 *
 * Example JSON:
 * {
 *     "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
 *     "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
 *     "tokenType": "Bearer",
 *     "expiresInSeconds": 3600
 * }
 *
 * @param accessToken     Short-lived token (used for API calls).
 * @param refreshToken    Long-lived token (used to get a new access token).
 * @param tokenType       Always "Bearer" — tells the client how to use the token.
 * @param expiresInSeconds How many seconds until the access token expires.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds
) {
}