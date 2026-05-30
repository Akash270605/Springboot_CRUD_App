// ============================================================
// FILE: PostResponse.java
// This is a "DTO" (Data Transfer Object) — a simple carrier
// that holds the data SENT BACK TO the client (browser/app)
// when they request information about a blog post.
//
// Unlike PostRequest (which only has input fields), this one
// includes extra fields that the database generates, like
// the post ID, timestamps, and the author's username.
// ============================================================
package com.example.crud.post;

import java.time.Instant;

/**
 * ---------------------------------------------------------------
 * POST RESPONSE DTO
 * ---------------------------------------------------------------
 * This record represents the JSON that will be sent to the
 * client when they fetch a post or list of posts.
 *
 * Example JSON the client will receive:
 * {
 *     "id": 1,
 *     "title": "My Blog Post",
 *     "content": "This is the content...",
 *     "published": true,
 *     "authorUsername": "alice",
 *     "createdAt": "2026-05-30T10:00:00Z",
 *     "updatedAt": "2026-05-30T12:00:00Z"
 * }
 *
 * @param id             The unique database ID of the post.
 * @param title          The post headline.
 * @param content        The main body text.
 * @param published      Whether the post is visible to the public.
 * @param authorUsername The username of the person who wrote the post.
 * @param createdAt      Timestamp when it was first created.
 * @param updatedAt      Timestamp of the last edit.
 */
public record PostResponse(
        Long id,
        String title,
        String content,
        boolean published,
        String authorUsername,
        Instant createdAt,
        Instant updatedAt
) {
}