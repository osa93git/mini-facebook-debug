# 🔧 Mini-Facebook – Backend

REST API zbudowane w Spring Boot, odpowiadające za rejestrację użytkowników, zarządzanie profilami i obsługę postów. Backend łączy się z bazą danych PostgreSQL uruchamianą w kontenerze Docker.

---

## 🧩 Stack technologiczny (backend)

- Java 21 (LTS)
- Spring Boot
- Spring Web (REST API)
- Spring Data JPA
- PostgreSQL (Docker)
- (planowane) Spring Security

---

## 📁 Struktura katalogów (warstwy)

```plaintext
com/ossowski/backend/
├── controller/   # REST API
├── model/        # Encje JPA
├── repository/   # Interfejsy do bazy
├── service/      # Logika biznesowa (opcjonalnie)
└── BackendApplication.java
```