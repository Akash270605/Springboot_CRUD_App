// ============================================================
// FILE: Role.java
// This file defines an "Enum" — a special Java type that
// represents a fixed set of constant values.
//
// Think of an enum like a dropdown menu: you can only pick
// from the options that are predefined here.
// ============================================================
package com.example.crud.user;

/**
 * ---------------------------------------------------------------
 * ROLE enum
 * ---------------------------------------------------------------
 * This enum lists all the possible user roles in the application.
 * Roles are used by Spring Security to decide what a user is
 * allowed to do (for example, an ADMIN can delete posts, but a
 * regular USER can only edit their own posts).
 *
 * Each role name starts with "ROLE_" because that is a convention
 * Spring Security expects.
 */
public enum Role {

    /** 👤 A standard, regular user (can create and edit their own posts). */
    ROLE_USER,

    /** 🛡️ An administrator (has elevated permissions, such as deleting any post). */
    ROLE_ADMIN
}