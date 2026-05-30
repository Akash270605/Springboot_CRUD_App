// ============================================================
// FILE: UserRepository.java
// This is a "Repository" interface — it provides methods to
// read, save, update, and delete User data from the database.
//
// Spring Data JPA automatically implements all the basic
// CRUD operations (like findAll, findById, save, delete)
// just by extending JpaRepository.
//
// We only need to define custom methods that aren't already
// provided out-of-the-box.
// ============================================================
package com.example.crud.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * ---------------------------------------------------------------
 * USER REPOSITORY
 * ---------------------------------------------------------------
 * Think of a Repository as a "data access layer" — it sits
 * between your Java code and the actual SQL database.
 *
 * By extending JpaRepository<User, Long>, we automatically get:
 *   - findAll()      → Get all users.
 *   - findById(id)   → Get one user by their ID.
 *   - save(user)     → Insert or update a user.
 *   - delete(user)   → Remove a user.
 *   - count()        → Count how many users exist.
 *
 * The two generic types are:
 *   <User, Long> → "I'm managing User objects, and their ID is a Long."
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     * Spring Data JPA understands the method name "findByUsername"
     * and automatically generates the correct SQL query.
     *
     * Example SQL: SELECT * FROM users WHERE username = ?;
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, or empty if not.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a username is already taken (used during registration).
     *
     * @param username The username to check.
     * @return true if a user with that username already exists.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an email address is already registered.
     *
     * @param email The email to check.
     * @return true if a user with that email already exists.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by username AND eagerly loads their posts at
     * the same time (using a SQL JOIN FETCH).
     *
     * Why "fetch"?
     * Normally, when you load a User, their "posts" list is loaded
     * lazily (only when you actually access it). This custom query
     * uses "JOIN FETCH" to load everything in a single database trip.
     *
     * @param username The username to search for.
     * @return An Optional containing the User (with their posts loaded) if found.
     */
    @Query("select u from User u left join fetch u.posts where u.username = :username")
    Optional<User> findByUsernameWithPosts(@Param("username") String username);
}