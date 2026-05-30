// ============================================================
// FILE: JwtService.java
// This is a "Service" class responsible for creating (signing)
// and reading (parsing) JWT tokens.
//
// JWT stands for "JSON Web Token". It's a digital ID card
// that proves who a user is. The token is cryptographically
// signed so it cannot be forged or tampered with.
// ============================================================
package com.example.crud.security;

import com.example.crud.auth.AuthResponse;
import com.example.crud.exception.BadRequestException;
import com.example.crud.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/**
 * ---------------------------------------------------------------
 * JWT SERVICE
 * ---------------------------------------------------------------
 * This service is responsible for creating (signing) and reading
 * (parsing) JWT tokens. Think of a JWT like a digital ID card
 * that the user carries.
 *
 * Key beginner concepts:
 * - Access Token  → Short-lived (minutes), used for every API call.
 * - Refresh Token → Long-lived (days), used only to get a new Access Token.
 * - Signing Key   → A secret password used to cryptographically sign the token.
 * - Claims        → Pieces of information stored inside the token
 *                   (like username, role, expiration date).
 */
@Service
public class JwtService {

    /**
     * An enum to distinguish between the two types of tokens we issue.
     */
    public enum TokenType {
        ACCESS, // Short-lived (minutes), used for API calls.
        REFRESH // Long-lived (days), used to get a new Access token.
    }

    private final JwtProperties properties;  // Configuration values (secret, durations).
    private final SecretKey signingKey;      // The cryptographic key to sign/verify tokens.

    /**
     * Constructor: builds the signing key from the secret string.
     *
     * @param properties Configuration containing the secret and token durations.
     */
    public JwtService(JwtProperties properties) {
        this.properties = properties;
        // Convert the secret text into a proper cryptographic key.
        // HMAC-SHA is a type of signing algorithm.
        this.signingKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates BOTH an Access Token and a Refresh Token for a user.
     *
     * @param user The user to generate tokens for.
     * @return An AuthResponse containing both tokens and metadata.
     */
    public AuthResponse tokensFor(User user) {
        String accessToken = generate(user, TokenType.ACCESS);
        String refreshToken = generate(user, TokenType.REFRESH);
        // expiresInSeconds tells the client when the access token will expire.
        return new AuthResponse(accessToken, refreshToken, "Bearer", properties.accessTokenMinutes() * 60);
    }

    /**
     * Extracts the username from a token, but only if the token
     * matches the expected type (ACCESS or REFRESH).
     *
     * @param token        The JWT string to parse.
     * @param expectedType Should be ACCESS or REFRESH.
     * @return The username stored inside the token.
     * @throws BadRequestException if the token type doesn't match or the token is invalid.
     */
    public String extractUsername(String token, TokenType expectedType) {
        Claims claims = parse(token);
        String tokenType = claims.get("type", String.class);
        if (!expectedType.name().equals(tokenType)) {
            throw new BadRequestException("Invalid token type");
        }
        return claims.getSubject(); // "subject" = the username in this case.
    }

    /**
     * Checks if an access token is valid for a given user.
     * It verifies that the username in the token matches the
     * user's actual username.
     *
     * @param token The access token to check.
     * @param user  The user we're comparing against.
     * @return true if the token belongs to this user.
     */
    public boolean isValidAccessToken(String token, User user) {
        return user.getUsername().equals(extractUsername(token, TokenType.ACCESS));
    }

    /**
     * Private helper: builds (signs) a JWT token with specific claims.
     *
     * @param user      The user this token is for.
     * @param tokenType ACCESS (short) or REFRESH (long).
     * @return A signed JWT string.
     */
    private String generate(User user, TokenType tokenType) {
        Instant now = Instant.now();
        // Calculate expiration: minutes for Access, days for Refresh.
        Instant expiresAt = tokenType == TokenType.ACCESS
                ? now.plus(properties.accessTokenMinutes(), ChronoUnit.MINUTES)
                : now.plus(properties.refreshTokenDays(), ChronoUnit.DAYS);

        // Build the JWT with:
        //   subject  = username (who the token belongs to)
        //   claims   = role + token type (extra info)
        //   issuedAt = when it was created
        //   expiration = when it expires
        //   signWith = cryptographically sign it
        return Jwts.builder()
                .subject(user.getUsername())            // Who the token belongs to.
                .claim("role", user.getRole().name())   // Store the user's role.
                .claim("type", tokenType.name())        // ACCESS or REFRESH.
                .issuedAt(Date.from(now))               // Creation timestamp.
                .expiration(Date.from(expiresAt))       // Expiration timestamp.
                .signWith(signingKey)                   // Cryptographically signs the token.
                .compact();                             // Produce the final JWT string.
    }

    /**
     * Private helper: verifies the cryptographic signature and
     * decodes the token's data (claims).
     *
     * If the token is expired, malformed, or has a bad signature,
     * an exception is thrown.
     *
     * @param token The JWT string to parse.
     * @return The claims (data) stored inside the token.
     * @throws BadRequestException if the token is invalid or expired.
     */
    private Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)         // Use the secret key to verify signature.
                    .build()
                    .parseSignedClaims(token)       // Parse and verify the token.
                    .getPayload();                  // Get the data inside.
        } catch (RuntimeException ex) {
            throw new BadRequestException("Invalid or expired token");
        }
    }
}