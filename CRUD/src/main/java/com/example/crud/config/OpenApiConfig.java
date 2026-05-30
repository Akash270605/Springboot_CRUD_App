// ============================================================
// FILE: OpenApiConfig.java
// This is a "Configuration" class that sets up Swagger/OpenAPI
// documentation for this REST API.
//
// Swagger automatically generates interactive API documentation
// that you can view in a web browser. It lets developers see
// all available endpoints, what data they expect, and even
// test them directly.
//
// When running the app, visit: http://localhost:8080/swagger-ui.html
// ============================================================
package com.example.crud.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ---------------------------------------------------------------
 * OPEN API / SWAGGER CONFIGURATION
 * ---------------------------------------------------------------
 * This configuration tells Swagger (the documentation tool)
 * about the security scheme used by this API.
 *
 * Since our API uses JWT Bearer tokens, we need to tell
 * Swagger to include an "Authorize" button where users can
 * paste their token and test authenticated endpoints.
 *
 * Key beginner concepts:
 * - OpenAPI      → The standard format for API documentation.
 * - Swagger UI   → A web interface for viewing/testing the API.
 * - SecurityScheme → Defines how authentication works (Bearer JWT).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates an OpenAPI bean that configures the Swagger
     * documentation page with:
     *   - API title and version info.
     *   - A "bearerAuth" security scheme (JWT).
     *   - A global security requirement (so all endpoints
     *     show the "Authorize" button by default).
     *
     * @return A configured OpenAPI object used by Swagger.
     */
    @Bean
    OpenAPI openAPI() {
        String schemeName = "bearerAuth";
        return new OpenAPI()
                // Basic API information.
                .info(new Info().title("CRUD API").version("v1"))
                // Tell Swagger that most endpoints need authentication.
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                // Define how the authentication works (Bearer JWT).
                .components(new Components().addSecuritySchemes(
                        schemeName,
                        new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)  // HTTP-based auth.
                                .scheme("bearer")                // "Bearer" scheme.
                                .bearerFormat("JWT")             // Using JWT tokens.
                ));
    }
}