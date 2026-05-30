// ============================================================
// FILE: Post.java
// This is an "Entity" class — it represents a single row in
// the "posts" database table. Each field becomes a column.
//
// A "Post" is a blog post that a user writes. It has a title,
// content, a published/draft status, and timestamps.
// ============================================================
package com.example.crud.post;

import com.example.crud.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * ---------------------------------------------------------------
 * POST Entity
 * ---------------------------------------------------------------
 * Maps to the "posts" table in the database.
 *
 * Key annotations for beginners:
 * - @Entity           → This class represents a database table.
 * - @Table(name=)     → The actual SQL table name.
 * - @Id               → Primary key field.
 * - @GeneratedValue   → Auto-increment.
 * - @Column           → Column constraints (nullable, length, etc.).
 * - @ManyToOne        → Many posts can belong to ONE user (the author).
 * - @JoinColumn       → The foreign key column linking to the "users" table.
 * - @PrePersist       → A method that runs automatically BEFORE saving for the first time.
 * - @PreUpdate        → A method that runs automatically BEFORE every update.
 */
@Entity
@Table(name = "posts")
public class Post {

    // ----------------------------------------------------------
    // PRIMARY KEY — unique ID for each post (auto-incremented).
    // ----------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----------------------------------------------------------
    // TITLE — the post headline (max 160 characters).
    // ----------------------------------------------------------
    @Column(nullable = false, length = 160)
    private String title;

    // ----------------------------------------------------------
    // CONTENT — the main body of the blog post.
    // "columnDefinition = text" means this is a TEXT column in
    // SQL, which can hold much more data than a regular VARCHAR.
    // ----------------------------------------------------------
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    // ----------------------------------------------------------
    // PUBLISHED — whether this post is visible to the public.
    // false = draft (only the author can see it).
    // true  = published (anyone can read it).
    // ----------------------------------------------------------
    @Column(nullable = false)
    private boolean published;

    // ----------------------------------------------------------
    // AUTHOR — the User who wrote this post.
    //
    // @ManyToOne means: "Many posts can belong to one user."
    // fetch = FetchType.LAZY → Don't load the author's full data
    //      until we actually ask for it (saves memory).
    // optional = false → A post MUST have an author.
    //
    // @JoinColumn(name = "author_id") → Creates a foreign key
    //      column named "author_id" that references the "users" table.
    // ----------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // ----------------------------------------------------------
    // CREATED AT — timestamp when the post was first created.
    // ----------------------------------------------------------
    @Column(nullable = false)
    private Instant createdAt;

    // ----------------------------------------------------------
    // UPDATED AT — timestamp of the most recent edit.
    // ----------------------------------------------------------
    @Column(nullable = false)
    private Instant updatedAt;

    // ==========================================================
    // LIFECYCLE CALLBACKS (triggered automatically by JPA)
    // ==========================================================

    /**
     * This method runs automatically JUST BEFORE this entity
     * is saved to the database for the very first time.
     * It sets the "createdAt" and "updatedAt" timestamps to "now".
     */
    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * This method runs automatically JUST BEFORE any update.
     * It refreshes the "updatedAt" timestamp to "now".
     */
    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    // ==========================================================
    // GETTERS & SETTERS
    // (Standard pattern for reading and writing private fields.)
    // ==========================================================

    /** @return The unique database ID of this post. */
    public Long getId() {
        return id;
    }

    /** @return The title/headline of the blog post. */
    public String getTitle() {
        return title;
    }

    /** Sets the title of the blog post. */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return The main content/body of the blog post. */
    public String getContent() {
        return content;
    }

    /** Sets the content/body of the blog post. */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Checks if this post is published (visible to everyone).
     * @return true if published, false if it's a draft.
     */
    public boolean isPublished() {
        return published;
    }

    /** Marks the post as published (true) or draft (false). */
    public void setPublished(boolean published) {
        this.published = published;
    }

    /** @return The User who wrote this post. */
    public User getAuthor() {
        return author;
    }

    /** Assigns a user as the author of this post. */
    public void setAuthor(User author) {
        this.author = author;
    }

    /** @return The timestamp when this post was first created. */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /** @return The timestamp of the most recent update to this post. */
    public Instant getUpdatedAt() {
        return updatedAt;
    }
}