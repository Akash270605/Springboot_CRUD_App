// ============================================================
// FILE: PostService.java
// This is a "Service" class — it contains the core business
// logic (also called "business rules") for managing blog posts.
//
// The Service layer sits between:
//   - The Controller (which handles HTTP requests/responses)
//   - The Repository (which talks to the database)
//
// Think of it as the "brain" of the application — it decides
// what is allowed and what isn't.
// ============================================================
package com.example.crud.post;

import com.example.crud.exception.ForbiddenException;
import com.example.crud.exception.ResourceNotFoundException;
import com.example.crud.user.Role;
import com.example.crud.user.User;
import com.example.crud.user.UserRepository;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ---------------------------------------------------------------
 * POST SERVICE
 * ---------------------------------------------------------------
 * This service contains the "Business Rules" for Blog Posts.
 * It bridges the Gap between the API Controller and the Database.
 *
 * Key beginner concepts:
 * - @Service            → Marks this as a Spring-managed service bean.
 * - @Transactional      → Database operations either fully succeed or fully roll back.
 *   (readOnly = true)   → For read-only queries (better performance).
 *
 * The service enforces rules like:
 *   - Only the author (or an admin) can update or delete a post.
 *   - A post must have an author assigned when created.
 */
@Service
public class PostService {

    // Dependencies (injected via constructor):
    private final PostRepository postRepository;   // For database access.
    private final UserRepository userRepository;   // For looking up users.
    private final PostMapper postMapper;           // For converting between DTOs and entities.

    /**
     * Constructor-based dependency injection.
     * Spring automatically provides (injects) the three objects we need.
     *
     * @param postRepository Handles database operations for posts.
     * @param userRepository Handles database operations for users.
     * @param postMapper     Converts between PostRequest/Post/PostResponse.
     */
    public PostService(PostRepository postRepository, UserRepository userRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postMapper = postMapper;
    }

    /**
     * Fetches all posts (or filters by search term if provided).
     * This is a READ-ONLY operation — we never modify data here.
     *
     * @param search Optional keyword to search titles/content for.
     * @return A list of PostResponse DTOs.
     */
    @Transactional(readOnly = true)
    public List<PostResponse> findAll(String search) {
        // If no search term is given, fetch ALL posts.
        // Otherwise, search for matching posts.
        List<Post> posts = search == null || search.isBlank()
                ? postRepository.findAll()
                : postRepository.search(search);
        // Convert each Post entity into a PostResponse DTO.
        return posts.stream().map(postMapper::toResponse).toList();
    }

    /**
     * Fetches only posts that are marked as "published".
     * This is a READ-ONLY operation.
     *
     * @return A list of published PostResponse DTOs.
     */
    @Transactional(readOnly = true)
    public List<PostResponse> findPublished() {
        return postRepository.findPublishedPosts().stream()
                .map(postMapper::toResponse)
                .toList();
    }

    /**
     * Finds a single post by its ID.
     *
     * @param id The database ID of the post.
     * @return The PostResponse for the found post.
     * @throws ResourceNotFoundException if no post with that ID exists.
     */
    @Transactional(readOnly = true)
    public PostResponse findOne(Long id) {
        return postMapper.toResponse(findPost(id));
    }

    /**
     * Creates a new blog post and assigns the currently logged-in
     * user as the author.
     *
     * @param request        The post data from the client (title, content, published).
     * @param authentication The security context containing the logged-in user.
     * @return The PostResponse of the newly created post.
     */
    @Transactional
    public PostResponse create(PostRequest request, Authentication authentication) {
        User author = currentUser(authentication);               // Get the logged-in user.
        Post post = postMapper.toEntity(request);                // Convert DTO to database entity.
        post.setAuthor(author);                                  // Assign the author.
        return postMapper.toResponse(postRepository.save(post)); // Save to DB and return response.
    }

    /**
     * Updates an existing post, but only if the requester is
     * the original author OR has the ADMIN role.
     *
     * @param id             The ID of the post to update.
     * @param request        The new data for the post.
     * @param authentication The security context of the requester.
     * @return The updated PostResponse.
     */
    @Transactional
    public PostResponse update(Long id, PostRequest request, Authentication authentication) {
        Post post = findPost(id);                               // 1. Find the post.
        requireOwnerOrAdmin(post, authentication);              // 2. Security check.
        postMapper.updateEntity(request, post);                 // 3. Apply new data.
        return postMapper.toResponse(post);                     // 4. Return updated version.
    }

    /**
     * Deletes a post after verifying the user has permission.
     * Only the author or an admin can delete a post.
     *
     * @param id             The ID of the post to delete.
     * @param authentication The security context of the requester.
     */
    @Transactional
    public void delete(Long id, Authentication authentication) {
        Post post = findPost(id);                  // 1. Find the post.
        requireOwnerOrAdmin(post, authentication); // 2. Security check.
        postRepository.delete(post);               // 3. Delete from database.
    }

    // ==========================================================
    // PRIVATE HELPER METHODS
    // (Internal helpers used by the public methods above.)
    // ==========================================================

    /**
     * Finds a post by ID, or throws an exception if it doesn't exist.
     *
     * @param id The ID to look up.
     * @return The Post entity.
     * @throws ResourceNotFoundException if the post is not in the database.
     */
    private Post findPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + id));
    }

    /**
     * Gets the full User object for the currently logged-in person.
     *
     * @param authentication Spring Security's authentication object.
     * @return The User entity from the database.
     * @throws ResourceNotFoundException if the user somehow doesn't exist.
     */
    private User currentUser(Authentication authentication) {
        String username = authentication.getName();  // Get the username from the JWT token.
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    /**
     * SECURITY CHECK: Ensures the user has permission to modify a post.
     *
     * A user is allowed if EITHER:
     *   1. They are the original author (owner) of the post, OR
     *   2. They have the ADMIN role.
     *
     * If neither condition is met, a ForbiddenException is thrown.
     *
     * @param post           The post being modified.
     * @param authentication The security context of the requester.
     * @throws ForbiddenException if the user is not the owner or an admin.
     */
    private void requireOwnerOrAdmin(Post post, Authentication authentication) {
        User user = currentUser(authentication);
        boolean isOwner = post.getAuthor().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You are not allowed to modify this post");
        }
    }
}