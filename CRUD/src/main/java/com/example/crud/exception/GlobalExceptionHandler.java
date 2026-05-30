// ============================================================
// FILE: GlobalExceptionHandler.java
// This class acts as a central hub for catching errors across
// the entire application.
//
// Instead of every controller having try-catch blocks, this
// class "intercepts" exceptions and turns them into clean,
// standardized JSON responses using the RFC 7807 "Problem
// Details" format.
//
// Think of it as a "safety net" that catches every error
// and gives back a friendly, consistent error message.
// ============================================================
package com.example.crud.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ---------------------------------------------------------------
 * GLOBAL EXCEPTION HANDLER
 * ---------------------------------------------------------------
 * This class acts as a central hub for catching errors across
 * the entire application.
 * Instead of every controller having try-catch blocks, this class
 * "intercepts" exceptions and turns them into clean, standardized
 * JSON responses.
 *
 * Key beginner concepts:
 * - @RestControllerAdvice → "Listen to all controllers and handle
 *   their exceptions here."
 * - @ExceptionHandler     → "If THIS type of exception occurs,
 *   run THIS method."
 * - ProblemDetail         → RFC 7807 standard format for error responses.
 *
 * Error mapping:
 *   ResourceNotFoundException  → 404 Not Found
 *   BadRequestException        → 400 Bad Request
 *   ForbiddenException         → 403 Forbidden
 *   BadCredentialsException    → 401 Unauthorized
 *   MethodArgumentNotValidException → 400 with field-level error details
 *   Exception (catch-all)      → 500 Internal Server Error
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Triggered when a specific resource (like a Post or User)
     * isn't found in the database.
     * Returns HTTP 404 Not Found.
     *
     * @param ex      The exception that was thrown.
     * @param request The incoming HTTP request (used for the "instance" URI).
     * @return A ProblemDetail with HTTP 404 status.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage(), request);
    }

    /**
     * Triggered when the user sends a request that doesn't make
     * sense or is malformed (e.g., registering with a duplicate username).
     * Returns HTTP 400 Bad Request.
     *
     * @param ex      The exception that was thrown.
     * @param request The incoming HTTP request.
     * @return A ProblemDetail with HTTP 400 status.
     */
    @ExceptionHandler(BadRequestException.class)
    ProblemDetail badRequest(BadRequestException ex, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage(), request);
    }

    /**
     * Triggered when a user tries to access something they don't
     * have permission for (e.g., a regular user trying to edit
     * another user's post).
     * Returns HTTP 403 Forbidden.
     *
     * @param ex      The exception that was thrown.
     * @param request The incoming HTTP request.
     * @return A ProblemDetail with HTTP 403 status.
     */
    @ExceptionHandler(ForbiddenException.class)
    ProblemDetail forbidden(ForbiddenException ex, HttpServletRequest request) {
        return problem(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage(), request);
    }

    /**
     * Triggered when login fails (wrong username or password).
     * Returns HTTP 401 Unauthorized.
     *
     * @param ex      The exception that was thrown.
     * @param request The incoming HTTP request.
     * @return A ProblemDetail with HTTP 401 status.
     */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    ProblemDetail unauthorized(RuntimeException ex, HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid credentials", request);
    }

    /**
     * Triggered when @Valid checks fail on a request body.
     * For example, if a required field is blank.
     * Returns HTTP 400 Bad Request WITH a detailed list of which
     * fields failed and why.
     *
     * @param ex      The validation exception.
     * @param request The incoming HTTP request.
     * @return A ProblemDetail with HTTP 400 and field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Validation failed",
                "Request validation failed", request);
        // Collect all validation errors (which field failed and why) into a map.
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        detail.setProperty("errors", errors);
        return detail;
    }

    /**
     * A "catch-all" for any other unexpected errors (like database
     * connection issues, null pointers, etc.).
     * Returns HTTP 500 Internal Server Error.
     *
     * @param ex      The exception that was thrown.
     * @param request The incoming HTTP request.
     * @return A ProblemDetail with HTTP 500 status.
     */
    @ExceptionHandler(Exception.class)
    ProblemDetail serverError(Exception ex, HttpServletRequest request) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                ex.getMessage(), request);
    }

    /**
     * Helper method to build a standardized "ProblemDetail" object
     * following the RFC 7807 standard.
     *
     * Example JSON output:
     * {
     *     "type": "https://api.example.com/problems/404",
     *     "title": "Resource not found",
     *     "status": 404,
     *     "detail": "Post not found: 999",
     *     "instance": "/api/posts/999"
     * }
     *
     * @param status   The HTTP status code (e.g., 404, 400).
     * @param title    A short, human-readable title for the error.
     * @param detail   A detailed description of what went wrong.
     * @param request  The HTTP request (used to set the "instance" field).
     * @return A configured ProblemDetail object.
     */
    private ProblemDetail problem(HttpStatus status, String title, String detail,
                                   HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://api.example.com/problems/" + status.value()));
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}