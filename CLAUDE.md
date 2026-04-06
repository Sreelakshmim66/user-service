# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build (also compiles .proto files via protobuf-maven-plugin)
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=UserServiceTest
```

## Architecture

This is a Spring Boot 3.3.8 microservice (Java 17) that provides user management with two interfaces:

**REST API** (port 8081) — handled by `UserController` → `UserService`:
- `POST /api/auth/register` — public, creates a user
- `POST /api/auth/login` — public, returns JWT
- `GET /api/users/{userId}` — protected, requires Bearer token
- `GET /api/health` — public health check

**gRPC server** (port 9091) — handled by `UserGrpcService`:
- `GetUser` — returns full user details by userId
- `ValidateUser` — checks if a userId exists

The service registers with Eureka at `http://localhost:8761/eureka`.

## Security Flow

All requests to protected endpoints pass through `JwtAuthenticationFilter` (a `OncePerRequestFilter`). The filter skips `/api/auth/**` and `/api/health`. For other requests, it extracts the Bearer token, calls `JwtService.validateAndExtractUserId()`, then loads the user via `CustomUserDetailsService.loadUserByUsername(userId)` to populate the `SecurityContext`.

JWT tokens use HMAC-SHA256 with the secret from `jwt.secret` (Base64-encoded) and embed `userId` as the subject. Expiry is controlled by `jwt.expiration-ms` (default: 86400000ms = 24h).

## Proto / gRPC Code Generation

The `.proto` file lives at `src/main/proto/user.proto`. The `protobuf-maven-plugin` compiles it during the build — generated classes (stubs, request/response types) are emitted into the `com.internalproject.user_service.grpc` package. After changing `user.proto`, run `./mvnw generate-sources` to regenerate before editing `UserGrpcService`.

## Key Configuration

`src/main/resources/application.properties`:
- PostgreSQL: `localhost:5433` (non-default port), database `User-service`
- `spring.jpa.hibernate.ddl-auto=update` — schema is auto-managed

## Data Model

`User` entity (`users` table) uses a `String` UUID as the primary key (`user_id`), set manually in `UserService.createUser()` via `UUID.randomUUID()`. Passwords are BCrypt-hashed before persistence.
