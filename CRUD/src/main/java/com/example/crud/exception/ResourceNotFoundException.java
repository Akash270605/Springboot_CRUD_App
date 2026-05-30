// ============================================================
// FILE: ResourceNotFoundException.java
// This is a custom "Exception" class.
//
// When we throw this exception, it means we tried to look up
// something in the database (like a blog post by its ID) but
// couldn't find it.
//
// The GlobalExceptionHandler catches this and returns
// HTTP 404 (Not Found) to the client.
// ============================================================
package com.example.crud.exception;

/**
 * ---------------------------------------------------------------
 * RESOURCE NOT FOUND EXCEPTION
 * ---------------------------------------------------------------
 * Thrown when a requested resource does not exist, such as:
 *   - Looking up a post by an ID that doesn't exist.
 *   - Looking up a user by a username that doesn't exist.
 *
 * This results in an HTTP 404 (Not Found) response.
 *
 * extends RuntimeException → "unchecked" exception.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new ResourceNotFoundException with a descriptive message.
     *
     * @param message A human-readable explanation of what was not found.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}