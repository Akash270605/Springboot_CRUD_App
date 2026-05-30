// ============================================================
// FILE: User.java
// This is an "Entity" class — it represents a row in the
// "users" database table. Each field here becomes a column
// in that table.
//
// This class also implements UserDetails, which is Spring
// Security's way of saying "this object can be used to
// represent a logged-in user."
// ============================================================
package com.example.crud.user;

import com.example.crud.post.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * ---------------------------------------------------------------
 * USER Entity
 * ---------------------------------------------------------------
 * This class maps to the "users" table in the database.
 * Every time a new user registers, a new row is saved here.
 *
 * "implements UserDetails" tells Spring Security:
 * "Hey, this object can be used to represent a user who wants
 *  to log in."
 *
 * Key concepts for beginners:
 * - @Entity        → Marks this class as a database table.
 * - @Table(name=)  → Specifies the actual table name in SQL.
 * - @Id            → The primary key (unique identifier).
 * - @GeneratedValue→ Auto-increment the ID.
 * - @Column        → Constraints like "cannot be null" or max length.
 * - @Enumerated    → Store the enum as a text string in the DB.
 * - @OneToMany     → One user can have MANY posts (1-to-Many relationship).
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    // ----------------------------------------------------------
    // PRIMARY KEY — every row needs a unique ID.
    // ----------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----------------------------------------------------------
    // USERNAME — used for logging in. Must be unique.
    // ----------------------------------------------------------
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // ----------------------------------------------------------
    // EMAIL — also unique. Used for account recovery, etc.
    // ----------------------------------------------------------
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    // ----------------------------------------------------------
    // PASSWORD — stores the BCrypt-hashed password (never plain text!).
    // ----------------------------------------------------------
    @Column(nullable = false)
    private String password;

    // ----------------------------------------------------------
    // ROLE — determines what this user is allowed to do.
    // Defaults to ROLE_USER (regular user).
    // ----------------------------------------------------------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role = Role.ROLE_USER;

    // ----------------------------------------------------------
    // CREATED AT — a timestamp of when this user signed up.
    // Uses Instant (modern Java time API).
    // ----------------------------------------------------------
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // ----------------------------------------------------------
    // POSTS — a list of blog posts written by this user.
    // "mappedBy = author" tells JPA that the "author" field in
    // the Post class owns this relationship.
    // cascade = ALL means: if we delete a User, also delete
    // all of their posts.
    // orphanRemoval = true removes posts that are no longer
    // linked to any user.
    // ----------------------------------------------------------
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // ==========================================================
    // GETTERS & SETTERS
    // (Standard JavaBean pattern — these let other code safely
    //  read and write the private fields.)
    // ==========================================================

    /** @return The unique database ID of this user. */
    public Long getId() {
        return id;
    }

    /**
     * Returns the username (required by Spring Security's UserDetails).
     * @return The username used for login.
     */
    public String getUsername() {
        return username;
    }

    /** Sets a new username for this user. */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return The user's email address. */
    public String getEmail() {
        return email;
    }

    /** Updates the email address. */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the list of authorities (roles/permissions) this user has.
     * Spring Security calls this method to check "is this user allowed?"
     * We wrap the role (e.g., ROLE_ADMIN) into a format Spring understands.
     *
     * @return A collection containing a single GrantedAuthority (the user's role).
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Returns the hashed password (required by UserDetails).
     * @return The BCrypt-encoded password string.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /** Sets the password (the service layer should hash it first!). */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return The role (USER or ADMIN) assigned to this account. */
    public Role getRole() {
        return role;
    }

    /** Assigns a new role (e.g., upgrading a user to ADMIN). */
    public void setRole(Role role) {
        this.role = role;
    }

    /** @return The timestamp when this user account was created. */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** @return The list of blog posts authored by this user. */
    public List<Post> getPosts() {
        return posts;
    }
}