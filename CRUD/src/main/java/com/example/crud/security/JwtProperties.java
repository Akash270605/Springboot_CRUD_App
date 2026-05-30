// ============================================================
// FILE: JwtProperties.java
// This is a "Configuration Properties" record.
//
// It reads values from the application's configuration files
// (like application.yml or application.properties) that start
// with "app.jwt".
//
// For example, in application.properties you'd have:
//   app.jwt.secret=my-super-secret-key...
//   app.jwt.access-token-minutes=60
//   app.jwt.refresh-token-days=7
//
// Spring Boot automatically fills these fields based on the
// prefix "app.jwt" defined in @ConfigurationProperties.
// ============================================================
package com.example.crud.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ---------------------------------------------------------------
 * JWT PROPERTIES
 * ---------------------------------------------------------------
 * This record holds the JWT configuration values that are
 * defined in your application properties file.
 *
 * @param secret             The cryptographic secret key used to sign JWT tokens.
 * @param accessTokenMinutes How long the Access Token is valid (in minutes).
 * @param refreshTokenDays   How long the Refresh Token is valid (in days).
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long accessTokenMinutes,
        long refreshTokenDays
) {
}