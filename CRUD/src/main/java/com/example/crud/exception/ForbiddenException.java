// ============================================================
// FILE: ForbiddenException.java
// This is a custom "Exception" class.
//
// When we throw this exception, it means a user tried to
// do something they don't have permission for (like trying
// to edit another user's post).
//
// The GlobalExceptionHandler catches this and returns
// HTTP 403 (Forbidden) to the client.
// ============================================================
package com.example.crud.exception;

/**
 * ---------------------------------------------------------------
 * FORBIDDEN EXCEPTION
 * ---------------------------------------------------------------
 * Thrown when a user attempts an action they are not allowed
 * to perform, such as:
 *   - A regular user trying to update another user's post.
 *   - A non-admin user trying to delete a post.
 *
 * This results in an HTTP 403 (Forbidden) response.
 *
 * extends RuntimeException → "unchecked" exception.
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Creates a new ForbiddenException with a descriptive message.
     *
     * @param message A human-readable explanation of why access is denied.
     */
    public ForbiddenException(String message) {
        super(message);
    }
}