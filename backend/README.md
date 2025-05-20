# 🔧 Mini-Facebook – Backend

This is the backend of the Mini-Facebook project – a REST API built with Spring Boot.  
It handles user registration, profile management, and post operations.  
The backend communicates with a PostgreSQL database running inside a Docker container.

---

## 🧩 Backend Tech Stack

- Java 21 (LTS)
- Spring Boot
- Spring Web (REST API)
- Spring Data JPA
- PostgreSQL (via Docker)
- (planned) Spring Security

---

## 📁 Package Structure (layers)

```plaintext
com/ossowski/backend/
├── controller/   # REST API
├── model/        # JPA entities
├── repository/   # Database access interfaces
├── service/      # Business logic (optional)
└── BackendApplication.java
```

## ✅ Implemented Features – Users

- [x] `GET /users` – list of all users (public data only)
- [x] `GET /users/{id}` – public user profile by UUID
- [x] `UserPublicDto` – shared DTO for both endpoints (no email, no password)
- [x] Refactored project structure to production layout (`controller`, `service`, `repository`, `user`)
- [x] Removed temporary `UserResponseDto` class
- [x] Added getter and setter for `bio` field in `User` entity
- [x] `GET /users/me` – current logged-in user (temporary static ID for now)

## ✅ Implemented Features – Security

- [x] Integrated Spring Security with stateless JWT authentication
- [x] `POST /auth/login` – user authentication, returns JWT token
- [x] `JwtAuthenticationFilter` – extracts and validates token from `Authorization` header
- [x] `SecurityConfig` – permits `/auth/**`, protects `/users/me` and other endpoints
- [x] `CustomUserDetailsService` – loads users from DB by email
- [x] `JwtService` – token generation and verification

### ✅ Added JWT Authentication with Refresh Token Support

- [x] `POST /auth/login` – user authentication, returns access token in JSON, refresh token in HttpOnly cookie
- [x] `POST /auth/refresh` – issues new access token if valid refresh token cookie is present
- [x] `JwtService` – access token includes userId, firstName, lastName, photo; refresh token minimal
- [x] `TokenService` – manages token persistence, revocation, validation
- [x] `Token` entity – stores token value, type, status, user owner
- [x] `SecurityConfig` – disables sessions, allows `/auth/**`, enforces JWT on other endpoints
- [x] `JwtAuthenticationFilter` – extracts and validates token from `Authorization: Bearer ...` header
- [x] `CustomUserDetailsService` – loads `User` entity from DB via email, implements `UserDetails`
- [x] `PasswordEncoder` – uses BCrypt to store hashed passwords in DB
- [x] `LoginRequest` / `LoginResponse` DTOs – used for clean JSON login flow