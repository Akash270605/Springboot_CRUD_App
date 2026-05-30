// ============================================================
// FILE: DataInitializer.java
// This is a "Configuration" class that runs automatically when
// the application starts.
//
// Its job is to seed the database with some initial data — in
// this case, a default ADMIN user so that someone can log in
// and manage the application right away.
// ============================================================
package com.example.crud.config;

import com.example.crud.user.Role;
import com.example.crud.user.User;
import com.example.crud.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ---------------------------------------------------------------
 * DATA INITIALIZER
 * ---------------------------------------------------------------
 * This class creates a default admin user when the application
 * starts for the first time.
 *
 * Key beginner concepts:
 * - @Configuration     → Marks this as a Spring configuration class.
 * - CommandLineRunner  → A piece of code that runs AFTER the
 *   application context is loaded (on startup).
 * - @Bean              → Declares a Spring-managed object.
 *
 * The default admin credentials are:
 *   Username: admin
 *   Password: Admin@12345
 */
@Configuration
public class DataInitializer {

    /**
     * Creates a bean that runs once at startup.
     * If an admin user already exists, nothing happens.
     * Otherwise, a default admin account is created.
     *
     * @param userRepository   Used to check/create users in the database.
     * @param passwordEncoder  Used to hash the admin password.
     * @return A CommandLineRunner that executes the seeding logic.
     */
    @Bean
    CommandLineRunner createDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if the default admin already exists.
            if (userRepository.existsByUsername("admin")) {
                return; // Admin already exists — no need to create another one.
            }

            // Create the default admin user.
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("Admin@12345")); // Hash the password.
            admin.setRole(Role.ROLE_ADMIN); // Grant admin privileges.
            userRepository.save(admin);     // Save to the database.

            System.out.println("✅ Default admin user created: admin / Admin@12345");
        };
    }
}