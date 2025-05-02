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