# AGENTS.md

## Coding Arena Backend — AI Agent Guide

This document provides essential, actionable knowledge for AI coding agents to be productive in the Coding Arena backend codebase. It summarizes architecture, workflows, conventions, and integration points specific to this project.

---

## 1. System Architecture & Data Flow
- **Spring Boot (Java 17+)** REST API for managing programming competitions.
- **Core domains:**
  - **User, Team, Contest, Task, Submission** — see `/model/` for JPA entities and `/docs/db-er.md` for ER diagram.
  - **TaskAssignment**: Links teams to tasks, tracks status/penalties.
  - **ContestParticipant**: Links teams to contests, tracks participation.
- **API endpoints**: `/api/*` (see `README.md` for key routes and required `X-User-Id` header).
- **Authentication**: All endpoints (except registration) require `X-User-Id` HTTP header. Enforced by `UserIdInterceptor` (see `/interceptor/` and `/config/WebConfig.java`).
- **Business logic**: Encapsulated in `/service/` classes (e.g., `AssignmentService`, `SubmissionService`).
- **Code evaluation**: Integrates with **Judge0** via `JudgeService` (see `/service/JudgeService.java`).
- **Leaderboard**: Aggregated in real-time using custom query in `LeaderboardRepository`.

## 2. Developer Workflows
- **Build & Run (local/dev):**
  - Use Docker Compose: `sudo docker compose up -d --build` (see `docker-compose.yml`).
  - API: `http://localhost:8080` (DB and Judge0 infra auto-provisioned).
  - DB is seeded via `/src/main/resources/data.sql`.
- **Testing:**
  - Postman collection (`Coding_Arena_Flow.postman_collection.json`) covers full API lifecycle and edge cases.
  - Run tests: `./mvnw test` (uses H2 for context tests).
- **Configuration:**
  - App config: `/src/main/resources/application.properties`.
  - Judge0 endpoint: `judge0.api.url` property.

## 3. Project-Specific Conventions & Patterns
- **Entity relationships**: Always consult `/docs/db-er.md` for canonical data model.
- **Task assignment**: Teams can only have one locked task per tier at a time (see `AssignmentService`).
- **Penalties**: Surrendering a task applies a fixed penalty (see `AssignmentService.surrenderTask`).
- **Transient code**: Submitted code is never persisted, only evaluated in-memory (see `SubmissionService`).
- **Role-based admin**: Admin endpoints under `/api/admin/*` (see `AdminController`).
- **Error handling**: Uses `GlobalExceptionHandler` for API errors.
- **UUIDs**: Teams use UUIDs as primary keys (see `Team.java`).
// --- Password and DTO Security Conventions ---
- **Password security**: Registration endpoint (`POST /api/users/register`) accepts a `password` field in the request body (never `passwordHash`). Passwords are always hashed server-side using SHA-256 (see `PasswordHashUtil`, `RegisterUserRequest`).
- **No password exposure**: No API response ever exposes a password or password hash. All user, team, and submission responses use DTOs that explicitly exclude sensitive fields. Use only:
  - `UserResponseDto` for user registration and info
  - `TeamMemberResponseDto` for team membership endpoints
  - `SubmissionResponseDto` for submission endpoints
  - Never return raw JPA entities containing password fields
- **Duplicate team membership prevention**: Users cannot join the same team more than once. The `joinTeam` endpoint checks for existing membership and returns HTTP 409 CONFLICT with a clear error message if a duplicate join is attempted (see `TeamController`, `TeamMemberRepository`).
// ---

## 4. Integration Points
- **Judge0**: Remote code execution via REST (see `JudgeService`).
- **PostgreSQL**: Main DB, auto-migrated and seeded.
- **Redis**: Used by Judge0 infra (not directly by backend).

## 5. Key Files & Directories
- `/model/` — JPA entities (mirror ER diagram)
- `/service/` — Business logic
- `/controller/` — REST endpoints
- `/repository/` — Data access, custom queries
- `/docs/db-er.md` — Entity-Relationship diagram
- `/Coding_Arena_Flow.postman_collection.json` — API test scenarios
- `/docker-compose.yml` — Full stack orchestration

---

**For more details, always cross-reference the ER diagram and README.**

