# CRUD API

Spring Boot 3 REST API with JWT authentication, role-based access control, JPA/Hibernate, Flyway migrations, MapStruct DTO mapping, RFC 7807 error responses, Swagger UI, integration tests, MySQL JDBC, and Docker.

## Run In Apache NetBeans

1. Open NetBeans.
2. Choose `File > Open Project`.
3. Select this folder: `C:\Users\Leveno\OneDrive\Desktop\CRUD`.
4. Wait for Maven dependencies to load.
5. Right-click the project and choose `Run`, or run the main class `com.example.crud.CrudApiApplication`.

The default local run uses H2 in MySQL compatibility mode, so MySQL is not required for normal NetBeans runs.

Useful URLs:

- API: `http://localhost:8080/api/posts`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`

Default admin:

- username: `admin`
- password: `Admin@12345`

## Run With Docker

```bash
docker compose up --build
```

This starts MySQL and the API at `http://localhost:8080`.

The Docker MySQL setup creates a non-root application user:

- username: `crud`
- password: empty
- database: `cruddb`
- host port: `3307`

## API Flow

Register:

```http
POST /api/auth/register
Content-Type: application/json

{"username":"alice","email":"alice@example.com","password":"Password123"}
```

Login:

```http
POST /api/auth/login
Content-Type: application/json

{"username":"alice","password":"Password123"}
```

Create post:

```http
POST /api/posts
Authorization: Bearer <accessToken>
Content-Type: application/json

{"title":"First post","content":"Hello Spring Boot","published":true}
```

## Tests

From NetBeans, right-click the project and choose `Test`.

From a terminal with Maven installed:

```bash
mvn test
```
