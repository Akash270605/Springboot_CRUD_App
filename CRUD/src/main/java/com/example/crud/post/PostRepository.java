// ============================================================
// FILE: PostRepository.java
// This is a "Repository" interface — it provides methods to
// read, save, update, and delete Post data from the database.
//
// By extending JpaRepository, Spring Data JPA automatically
// provides basic CRUD operations (findAll, findById, save,
// delete, etc.) without us writing any SQL.
//
// The custom methods below define more specific queries using
// the @Query annotation with JPQL (Java Persistence Query Language).
// ============================================================
package com.example.crud.post;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * ---------------------------------------------------------------
 * POST REPOSITORY
 * ---------------------------------------------------------------
 * This interface connects our Java application to the "posts"
 * database table.
 *
 * The two generic types are:
 *   <Post, Long> → "I'm managing Post objects, and their ID is a Long."
 *
 * Methods inherited automatically from JpaRepository:
 *   - findAll()       → Get all posts.
 *   - findById(id)    → Get one post by its ID.
 *   - save(post)      → Insert or update a post.
 *   - delete(post)    → Remove a post.
 *   - count()         → Count total posts.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Fetches all posts that are marked as "published", ordered
     * newest-first. Uses "JOIN FETCH" to also load the author's
     * information in the same database query (avoids extra queries).
     *
     * @return A list of published posts (with their authors loaded).
     */
    @Query("select p from Post p join fetch p.author where p.published = true order by p.createdAt desc")
    List<Post> findPublishedPosts();

    /**
     * Searches published posts by matching a search term against
     * both the title and the content (case-insensitive).
     *
     * Example: Searching "spring" would match posts whose title
     * OR content contains the word "spring" (or "Spring", etc.).
     *
     * @param term The search keyword (e.g., "spring boot").
     * @return A list of matching posts, newest-updated first.
     */
    @Query("""
            select p from Post p join fetch p.author
            where lower(p.title) like lower(concat('%', :term, '%'))
               or lower(p.content) like lower(concat('%', :term, '%'))
            order by p.updatedAt desc
            """)
    List<Post> search(@Param("term") String term);

    /**
     * Finds all posts written by a specific user, ordered by
     * creation date (newest first).
     *
     * @param username The author's username.
     * @return A list of posts written by that user.
     */
    @Query("select p from Post p join fetch p.author where p.author.username = :username order by p.createdAt desc")
    List<Post> findByAuthorUsername(@Param("username") String username);
}