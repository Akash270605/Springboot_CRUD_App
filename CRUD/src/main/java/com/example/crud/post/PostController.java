// ============================================================
// FILE: PostController.java
// This is a "Controller" — it handles incoming HTTP requests
// and sends back HTTP responses.
//
// Think of it as the "Receptionist" of the application:
// - It listens for requests at the "/api/posts" URL.
// - It decides which method to call based on the HTTP method
//   (GET, POST, PUT, DELETE) and URL pattern.
// - It passes the work to the Service layer and returns the result.
// ============================================================
package com.example.crud.post;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ---------------------------------------------------------------
 * POST CONTROLLER
 * ---------------------------------------------------------------
 * This controller maps HTTP requests to Service methods.
 *
 * Key annotations for beginners:
 * - @RestController         → This class handles HTTP requests and returns JSON.
 * - @RequestMapping("/...") → All methods in this class share this URL prefix.
 * - @GetMapping             → Handles GET requests (reading data).
 * - @PostMapping            → Handles POST requests (creating new data).
 * - @PutMapping             → Handles PUT requests (updating existing data).
 * - @DeleteMapping          → Handles DELETE requests (removing data).
 * - @PathVariable           → Extracts a value from the URL path (e.g., /posts/5 → id=5).
 * - @RequestParam           → Extracts a query parameter from the URL (?search=...).
 * - @RequestBody            → Reads the JSON body from the request.
 * - @Valid                  → Triggers validation checks (@NotBlank, @Size, etc.).
 * - ResponseEntity          → Lets us control the HTTP status code and headers.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    // The service that contains our business logic.
    private final PostService postService;

    /**
     * Constructor injection — Spring provides the PostService automatically.
     *
     * @param postService The service that handles post operations.
     */
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * GET /api/posts
     * Fetches all posts. Optionally filters by a search query (?search=keyword).
     *
     * @param search (optional) A keyword to search titles and content.
     * @return A list of all (or matching) posts.
     */
    @GetMapping
    public List<PostResponse> findAll(@RequestParam(required = false) String search) {
        return postService.findAll(search);
    }

    /**
     * GET /api/posts/published
     * Fetches only posts that are marked as published (visible to everyone).
     *
     * @return A list of published posts.
     */
    @GetMapping("/published")
    public List<PostResponse> findPublished() {
        return postService.findPublished();
    }

    /**
     * GET /api/posts/{id}
     * Fetches a single post by its ID.
     *
     * Example: GET /api/posts/3 → Returns the post with ID = 3.
     *
     * @param id The post ID from the URL path.
     * @return The details of the requested post.
     */
    @GetMapping("/{id}")
    public PostResponse findOne(@PathVariable Long id) {
        return postService.findOne(id);
    }

    /**
     * POST /api/posts
     * Creates a new blog post (requires authentication).
     *
     * The @Valid annotation ensures the request body passes validation
     * (e.g., title is not blank).
     *
     * The Authentication parameter is automatically filled by Spring
     * Security with the currently logged-in user's info.
     *
     * ResponseEntity.created(...) returns HTTP 201 (Created) with a
     * "Location" header pointing to the new resource.
     *
     * @param request        The post data from the request body.
     * @param authentication The security context of the logged-in user.
     * @return HTTP 201 with the created post and a Location header.
     */
    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request, Authentication authentication) {
        PostResponse response = postService.create(request, authentication);
        // Return 201 Created with the URL of the new post in the Location header.
        return ResponseEntity.created(URI.create("/api/posts/" + response.id())).body(response);
    }

    /**
     * PUT /api/posts/{id}
     * Updates an existing post (requires authentication, and you must
     * be the owner or an admin).
     *
     * @param id             The ID of the post to update.
     * @param request        The new data for the post.
     * @param authentication The security context of the logged-in user.
     * @return The updated post.
     */
    @PutMapping("/{id}")
    public PostResponse update(@PathVariable Long id, @Valid @RequestBody PostRequest request, Authentication authentication) {
        return postService.update(id, request, authentication);
    }

    /**
     * DELETE /api/posts/{id}
     * Deletes a post (only admins can delete posts).
     *
     * ResponseEntity.noContent() returns HTTP 204 (No Content) — the
     * standard response for a successful delete.
     *
     * @param id             The ID of the post to delete.
     * @param authentication The security context of the logged-in user.
     * @return HTTP 204 with an empty body.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        postService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }
}