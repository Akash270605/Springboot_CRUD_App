// ============================================================
// FILE: PostRequest.java
// This is a "DTO" (Data Transfer Object) — a simple carrier
// that holds the data coming FROM the client (browser/app)
// when they want to CREATE or UPDATE a blog post.
//
// It uses a Java "record" which is a compact way to write
// a class that mainly holds data. Records automatically
// generate constructors, getters, equals(), hashCode(), etc.
//
// Validation annotations (@NotBlank, @Size) are checked
// automatically by Spring when this is used in a Controller.
// ============================================================
package com.example.crud.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ---------------------------------------------------------------
 * POST REQUEST DTO
 * ---------------------------------------------------------------
 * This record represents the JSON body that a client sends
 * when creating or updating a post.
 *
 * Example JSON the client would send:
 * {
 *     "title": "My Blog Post",
 *     "content": "This is the content...",
 *     "published": true
 * }
 *
 * @param title     The post title (required, max 160 characters).
 * @param content   The post body/content (required).
 * @param published Whether the post should be visible to everyone (default false).
 */
public record PostRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank String content,
        boolean published
) {
}