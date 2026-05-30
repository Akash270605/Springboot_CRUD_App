# Springboot_CRUD_App

A production-grade Spring Boot REST API featuring JWT authentication, role-based access control (RBAC), comprehensive CRUD operations for posts, and comprehensive API documentation. Built with best practices including exception handling, DTO mapping, database migrations, and containerized deployment.

## 🎯 Project Overview

This project demonstrates a professional REST API implementation with:
- Secure JWT-based authentication with refresh tokens
- Role-based authorization (ADMIN, USER)
- Complete CRUD operations for blog posts
- Comprehensive error handling with RFC 7807 compliant responses
- Automated database migrations using Flyway
- Auto-generated API documentation with Swagger UI
- Docker containerization for easy deployment
- Production-ready configurations

## ✨ Key Features

### Authentication & Security
- JWT token-based authentication with HS512 algorithm
- Refresh token mechanism for extended sessions
- Role-based access control (RBAC)
- BCrypt password hashing for security
- Stateless authentication design
- Global CORS configuration support

### Post Management
- Create, read, update, and delete posts
- Draft/publish workflow
- Full-text search across post titles and content
- Author tracking with cascading deletes
- Timestamp tracking (created_at, updated_at)
- Published posts endpoint for public access

### User Management
- User registration with email validation
- Login with credential verification
- Profile retrieval
- Admin and regular user roles
- User-specific post filtering

### API Features
- RESTful endpoint design
- Comprehensive validation (JSR-303)
- Pagination support for large datasets
- Global exception handling
- Swagger UI documentation
- Request/Response DTOs
- Proper HTTP status codes

### Database
- Flyway-managed database migrations
- MySQL 8.4 with MySQL compatibility mode
- Foreign key relationships with cascade deletes
- Database indexes for performance optimization
- H2 in-memory database for local development

### DevOps
- Docker & Docker Compose support
- Multi-stage builds for optimized images
- Health checks for service readiness
- Volume management for data persistence
- Environment-based configuration

## 🛠️ Technology Stack

| Category | Technology |
|----------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.3.5 |
| **Database** | MySQL 8.4 / H2 (dev) |
| **ORM** | Hibernate JPA |
| **Security** | Spring Security 6 |
| **Authentication** | JWT (JJWT 0.12.6) |
| **Mapping** | MapStruct 1.6.2 |
| **API Docs** | Springdoc OpenAPI 2.x |
| **Validation** | JSR-303 (Jakarta Validation) |
| **Migration** | Flyway Core & MySQL |
| **Build Tool** | Maven 3.8+ |
| **Containerization** | Docker & Docker Compose |


## Output

<img width="1920" height="1080" alt="Screenshot (30)" src="https://github.com/user-attachments/assets/ddbe7e97-a542-4640-89cd-c90a665653e1" />

<img width="1920" height="1080" alt="Screenshot (31)" src="https://github.com/user-attachments/assets/73c78116-d5e5-4799-8ddb-37ada0a4dbdb" />

<img width="1920" height="1080" alt="Screenshot (32)" src="https://github.com/user-attachments/assets/99b25015-1c5a-4cbe-808c-7b0300f85d13" />

<img width="1765" height="831" alt="Screenshot 2026-05-30 131758" src="https://github.com/user-attachments/assets/7b91919b-7e8c-4f85-badf-9213f03b1aac" />

<img width="471" height="437" alt="Screenshot 2026-05-30 132535" src="https://github.com/user-attachments/assets/d740f621-3d61-47f6-80a6-ef218e7ed8fd" />

<img width="858" height="363" alt="Screenshot 2026-05-30 133000" src="https://github.com/user-attachments/assets/4167d4d3-e619-4f7c-8c99-3f3fe2fc00b3" />

<img width="1453" height="755" alt="Screenshot 2026-05-30 140120" src="https://github.com/user-attachments/assets/ba4fac7e-aadf-4af2-b2b8-7af087442673" />
