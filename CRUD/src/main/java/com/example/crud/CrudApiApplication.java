// ============================================================
// FILE: CrudApiApplication.java
// This is the ENTRY POINT of the entire Spring Boot application.
//
// Think of it as the "main switch" that turns on the entire
// application. When you run this file, it:
//   1. Starts the embedded web server (Tomcat).
//   2. Loads all Spring configurations.
//   3. Connects to the database.
//   4. Starts listening for HTTP requests on port 8080.
// ============================================================
package com.example.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ---------------------------------------------------------------
 * CRUD API APPLICATION — MAIN CLASS
 * ---------------------------------------------------------------
 * This is the class that Spring Boot uses to launch the
 * application. The @SpringBootApplication annotation is a
 * shortcut that combines three annotations:
 *
 * 1. @Configuration        → Marks this as a source of bean definitions.
 * 2. @EnableAutoConfiguration → Tells Spring to automatically
 *    configure components based on the dependencies in pom.xml.
 * 3. @ComponentScan        → Tells Spring to look for other
 *    components (@Service, @Controller, @Repository, etc.)
 *    in the "com.example.crud" package and all sub-packages.
 *
 * @SpringBootApplication
 * public class CrudApiApplication {
 *
 * The "main" method is the standard Java entry point.
 * It calls SpringApplication.run() which starts everything.
 */
@SpringBootApplication
public class CrudApiApplication {

    /**
     * The main method — this is where the JVM starts executing.
     *
     * SpringApplication.run() bootstraps the entire Spring
     * application: it creates the Spring context, starts the
     * embedded web server, and begins accepting HTTP requests.
     *
     * @param args Command-line arguments passed when starting
     *             the application (e.g., --server.port=9090).
     */
    public static void main(String[] args) {
        SpringApplication.run(CrudApiApplication.class, args);
    }
}