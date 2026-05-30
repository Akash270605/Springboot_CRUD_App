// ============================================================
// FILE: CrudApiApplicationTests.java
// This is a TEST class — it contains automated tests that
// verify the application behaves correctly.
//
// These tests use Spring Boot's testing tools to simulate
// HTTP requests and check the responses, without actually
// needing to run the application on a real server.
//
// Think of tests as "automatic checklists" that make sure
// everything works before we ship the code.
// ============================================================
package com.example.crud;

import com.example.crud.post.Post;
import com.example.crud.post.PostRepository;
import com.example.crud.user.Role;
import com.example.crud.user.User;
import com.example.crud.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ---------------------------------------------------------------
 * CRUD API APPLICATION TESTS
 * ---------------------------------------------------------------
 * This class tests all the API endpoints using Spring's
 * MockMvc — a tool that lets us simulate HTTP requests and
 * check the responses without starting a real web server.
 *
 * Key beginner concepts:
 * - @SpringBootTest     → Loads the full application context for testing.
 * - @AutoConfigureMockMvc → Sets up MockMvc automatically.
 * - @Autowired          → Spring automatically injects these dependencies.
 * - @BeforeEach         → This method runs BEFORE every test.
 * - @Test               → Marks a method as a test case.
 *
 * What's tested:
 *   - User registration, login, token refresh
 *   - Creating, reading, updating, and deleting posts
 *   - Security: unauthorized access, role-based permissions
 *   - Validation: error messages for bad input
 *   - Search and filtering functionality
 */
@SpringBootTest
@AutoConfigureMockMvc
class CrudApiApplicationTests {

    // ==========================================================
    // INJECTED DEPENDENCIES
    // Spring provides these automatically because of @Autowired.
    // ==========================================================

    @Autowired
    MockMvc mockMvc; // Tool for simulating HTTP requests.

    @Autowired
    ObjectMapper objectMapper; // Tool for parsing JSON.

    @Autowired
    UserRepository userRepository; // Database access for users.

    @Autowired
    PostRepository postRepository; // Database access for posts.

    @Autowired
    PasswordEncoder passwordEncoder; // For hashing test passwords.

    // ==========================================================
    // SETUP — runs before each test
    // Clears the database and creates fresh test users.
    // ==========================================================

    @BeforeEach
    void setUp() {
        // Start with a clean database for each test.
        postRepository.deleteAll();
        userRepository.deleteAll();

        // Create three test users:
        createUser("admin", "admin@example.com", Role.ROLE_ADMIN);  // Admin user.
        createUser("alice", "alice@example.com", Role.ROLE_USER);   // Regular user.
        createUser("bob", "bob@example.com", Role.ROLE_USER);      // Another regular user.
    }

    // ==========================================================
    // AUTHENTICATION TESTS
    // ==========================================================

    /** Test: Registering a new user returns JWT tokens. */
    @Test
    void registerCreatesUserAndReturnsTokens() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"charlie","email":"charlie@example.com","password":"Password123"}
                                """))
                .andExpect(status().isCreated())                  // Expect HTTP 201.
                .andExpect(jsonPath("$.accessToken").isNotEmpty()) // Token must exist.
                .andExpect(jsonPath("$.refreshToken").isNotEmpty()); // Refresh token must exist.
    }

    /** Test: Registering with an existing username is rejected. */
    @Test
    void registerRejectsDuplicateUsername() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","email":"new@example.com","password":"Password123"}
                                """))
                .andExpect(status().isBadRequest())               // Expect HTTP 400.
                .andExpect(jsonPath("$.title").value("Bad request"));
    }

    /** Test: Logging in with correct credentials returns tokens. */
    @Test
    void loginReturnsJwtTokens() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"Password123"}
                                """))
                .andExpect(status().isOk())                       // Expect HTTP 200.
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    /** Test: Logging in with wrong password is rejected. */
    @Test
    void loginRejectsBadPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())             // Expect HTTP 401.
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    /** Test: Refreshing a token returns a new access token. */
    @Test
    void refreshReturnsNewTokens() throws Exception {
        String refreshToken = login("alice").get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())                       // Expect HTTP 200.
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    /** Test: The /me endpoint returns the correct user info. */
    @Test
    void meReturnsCurrentUser() throws Exception {
        String token = login("alice").get("accessToken").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    // ==========================================================
    // POST CRUD TESTS
    // ==========================================================

    /** Test: Creating a post without auth returns 401. */
    @Test
    void createPostRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson("Title", "Content", true)))
                .andExpect(status().isUnauthorized());
    }

    /** Test: An authenticated user can create a post. */
    @Test
    void authenticatedUserCanCreatePost() throws Exception {
        String token = login("alice").get("accessToken").asText();

        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson("First post", "Hello API", true)))
                .andExpect(status().isCreated())                  // Expect HTTP 201.
                .andExpect(header().exists("Location"))           // Location header must exist.
                .andExpect(jsonPath("$.authorUsername").value("alice"));
    }

    /** Test: The public can read and search posts. */
    @Test
    void publicCanReadAndSearchPosts() throws Exception {
        createPost("Spring Boot", "Testing MockMvc", true, "alice");
        createPost("Draft", "Hidden but searchable in demo API", false, "bob");

        mockMvc.perform(get("/api/posts").param("search", "spring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))             // Should find 1 match.
                .andExpect(jsonPath("$[0].title").value("Spring Boot"));
    }

    /** Test: The /published endpoint only returns published posts. */
    @Test
    void publishedEndpointReturnsOnlyPublishedPosts() throws Exception {
        createPost("Visible", "Published", true, "alice");
        createPost("Draft", "Not published", false, "alice");

        mockMvc.perform(get("/api/posts/published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))             // Only 1 published.
                .andExpect(jsonPath("$[0].title").value("Visible"));
    }

    /** Test: Fetching a non-existent post returns 404. */
    @Test
    void getMissingPostReturnsProblemDetail() throws Exception {
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound())                 // Expect HTTP 404.
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    /** Test: The owner can update their own post. */
    @Test
    void ownerCanUpdatePost() throws Exception {
        Post post = createPost("Old", "Old content", false, "alice");
        String token = login("alice").get("accessToken").asText();

        mockMvc.perform(put("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson("New", "New content", true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.published").value(true));
    }

    /** Test: A non-owner cannot update someone else's post. */
    @Test
    void nonOwnerCannotUpdatePost() throws Exception {
        Post post = createPost("Alice post", "Only Alice owns this", true, "alice");
        String token = login("bob").get("accessToken").asText();

        mockMvc.perform(put("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson("Taken", "Nope", true)))
                .andExpect(status().isForbidden());               // Expect HTTP 403.
    }

    /** Test: Only admins can delete posts. */
    @Test
    void deleteRequiresAdminRole() throws Exception {
        Post post = createPost("Alice post", "Delete me", true, "alice");
        String userToken = login("alice").get("accessToken").asText();
        String adminToken = login("admin").get("accessToken").asText();

        // Regular user should NOT be able to delete.
        mockMvc.perform(delete("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());               // Expect HTTP 403.

        // Admin SHOULD be able to delete.
        mockMvc.perform(delete("/api/posts/" + post.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());               // Expect HTTP 204.
    }

    /** Test: Validation errors return proper error details. */
    @Test
    void validationErrorsUseProblemDetail() throws Exception {
        String token = login("alice").get("accessToken").asText();

        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson("", "", true)))          // Empty title + content.
                .andExpect(status().isBadRequest())               // Expect HTTP 400.
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.title").exists());  // Title field error.
    }

    // ==========================================================
    // PRIVATE HELPER METHODS
    // (Used by the tests to set up data and make requests.)
    // ==========================================================

    /**
     * Logs in a user and returns the JSON response containing tokens.
     *
     * @param username The username to log in with.
     * @return A JsonNode with "accessToken" and "refreshToken" fields.
     * @throws Exception if the login fails.
     */
    private JsonNode login(String username) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"Password123\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response);
    }

    /**
     * Creates a test user in the database.
     *
     * @param username The username for the new user.
     * @param email    The email address.
     * @param role     The role (USER or ADMIN).
     * @return The saved User entity.
     */
    private User createUser(String username, String email, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode("Password123")); // All users share this password.
        return userRepository.save(user);
    }

    /**
     * Creates a test post in the database.
     *
     * @param title     The post title.
     * @param content   The post content.
     * @param published Whether the post is published.
     * @param username  The author's username.
     * @return The saved Post entity.
     */
    private Post createPost(String title, String content, boolean published, String username) {
        User author = userRepository.findByUsername(username).orElseThrow();
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setPublished(published);
        post.setAuthor(author);
        return postRepository.save(post);
    }

    /**
     * Builds a JSON string for a post request body.
     *
     * @param title     The post title.
     * @param content   The post content.
     * @param published Whether the post is published.
     * @return A JSON string like {"title":"...","content":"...","published":true}.
     */
    private String postJson(String title, String content, boolean published) {
        return """
                {"title":"%s","content":"%s","published":%s}
                """.formatted(title, content, published);
    }
}