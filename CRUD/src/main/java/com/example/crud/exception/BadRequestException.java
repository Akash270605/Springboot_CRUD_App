// ============================================================
// FILE: BadRequestException.java
// This is a custom "Exception" class.
//
// Exceptions are Java's way of saying "something went wrong."
// When we throw this exception, it means the client sent a
// request that doesn't make sense (like trying to register
// with a username that already exists).
//
// The GlobalExceptionHandler catches this and returns
// HTTP 400 (Bad Request) to the client.
// ============================================================
package com.example.crud.exception;

/**
 * ---------------------------------------------------------------
 * BAD REQUEST EXCEPTION
 * ---------------------------------------------------------------
 * Thrown when the client sends invalid data, such as:
 *   - Trying to register with an existing username.
 *   - Providing a malformed or expired JWT token.
 *   - Sending any request that doesn't meet the business rules.
 *
 * This results in an HTTP 400 (Bad Request) response.
 *
 * Extending RuntimeException means this is an "unchecked"
 * exception — we don't have to declare it in method signatures.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Creates a new BadRequestException with a descriptive message.
     *
     * @param message A human-readable explanation of what went wrong.
     */
    public BadRequestException(String message) {
        super(message);
    }
}