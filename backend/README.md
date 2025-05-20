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
- Spring Security (JWT)

---

## 📁 Package Structure (modular layout)

```plaintext
com/ossowski/backend/
├── admin/         # Admin-specific features (e.g. user moderation, analytics)
├── auth/          # Authentication controller and DTOs
├── init/          # DB seeder, test user data
├── security/      # Security configuration and logic
│   ├── auth/       # Login, refresh endpoints
│   ├── jwt/        # JWT token logic
│   ├── model/      # Token entity, enums
│   ├── repository/ # Token repository
│   └── service/    # TokenService, JwtService
├── user/          # User endpoints, DTOs, service, repository, model
└── BackendApplication.java
```

---

### ✅ Implemented Features – Users

- `GET /users` – list of all users (public data only)
- `GET /users/{id}` – public user profile by UUID
- `GET /users/me` – current logged-in user (from token)
- `UserPublicDto` – shared DTO for public user data
- Extended `User` entity with `bio`, `profilePhotoUrl`, `role`
- Added support for `Role` enum (`USER`, `ADMIN`)
- Initial data seeding via `init` package

---

### ✅ Implemented Features – Security

- Integrated Spring Security with stateless JWT authentication
- `POST /auth/login` – user authentication, returns JWT token
- `POST /auth/refresh` – refreshes access token via HttpOnly cookie
- `JwtAuthenticationFilter` – extracts and validates token from `Authorization` header
- `SecurityConfig` – permits `/auth/**`, protects other endpoints
- `CustomUserDetailsService` – loads users from DB by email
- `JwtService` – token generation, validation
- `TokenService` – token persistence, revocation
- `Token` entity – stores value, type (access/refresh), status, and user

---

### ✅ Implemented Features – Admin

- `admin` package introduced for role-restricted operations
- Created `AdminController` with placeholder endpoint ("admin panel")
- Added `Role.ADMIN` with selective access

🔜 **Planned:**

- Admin-only endpoints to delete users
- View visit logs

---

### ✅ JWT Authentication with Refresh Token Support

- Access token stored in `Authorization: Bearer ...` header
- Refresh token stored as `HttpOnly` cookie
- `POST /auth/login` returns both tokens
- `POST /auth/refresh` issues new access token if refresh token is valid
- Refresh tokens stored in DB, associated with user and token type

---

### 🧪 Testing & Running

- Run with Docker Compose: `docker-compose up`
- Default DB: PostgreSQL on port `5432`
- Preloaded users and roles available via `init/Seeder`

---

### 📌 Notes

- Passwords are stored securely using BCrypt
- Stateless JWT architecture ensures scalability
- Full role-based access control (RBAC) built-in
- Designed for separation of concerns via module-based packages

---

### 📦 Future Work

- Admin panel with user logs & analytics
- Post creation and feed
- Comments and reactions
- Friend requests and chat
