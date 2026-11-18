# Notification Delivery Incident — Java Backend Interview

A small, runnable **Java 21 / Spring Boot** application for a 30-minute remote backend interview. The application contains **intentional defects** that should be investigated through an assigned GitHub Issue. This is a simulation: the delivery client only logs messages and never sends real notifications.

**This application is not production software.** It must not be connected to a real provider or deployed publicly.

## Architecture

- Java 21, Spring Boot 3.4, Spring Data JPA, Flyway
- PostgreSQL 16
- A scheduled job reads due notification rows and invokes a simulated delivery provider
- JUnit 5 / Mockito unit tests, PostgreSQL integration tests
- GitHub Actions CI, optional GitHub Codespaces configuration

## Quick start

Prerequisites: JDK 21, Maven 3.9+, and Docker / Docker Compose.

```bash
docker compose up -d postgres
mvn test
mvn verify
mvn spring-boot:run -Dspring-boot.run.profiles=demo
```

The `demo` profile inserts one pending notification with key `demo-notification-001`. The simulated provider logs a successful send. The poll interval is 10 seconds.

**Note:** `mvn verify` includes `*IT` PostgreSQL integration tests and therefore requires a running database. `mvn test` runs unit tests only.

To disable scheduling while investigating:

```bash
NOTIFICATION_WORKER_ENABLED=false mvn spring-boot:run
```

## Project map

| Path | Purpose |
| --- | --- |
| `src/main/java/.../worker/NotificationDispatchJob.java` | Scheduled dispatch logic |
| `src/main/java/.../delivery/` | Provider boundary and log-only implementation |
| `src/main/java/.../persistence/` | Spring Data repository |
| `src/main/java/.../domain/` | Notification entity and state |
| `src/main/resources/db/migration/` | PostgreSQL schema |
| `src/test/` | Unit and baseline integration tests |
| `docs/CANDIDATE_GUIDE.md` | Instructions and expected deliverables |
| `.github/workflows/ci.yml` | CI checks |

## Interview instructions

1. Open your assigned GitHub Issue.
2. Read the relevant code and repository history.
3. Ask focused questions, document assumptions, and make a minimal safe improvement if time permits.
4. Open a **Draft Pull Request** with clear findings, verification, risks, and a next-step handoff.
5. The interviewer will share an extra constraint and a review question during the 30-minute session.

Completion of the entire fix is **not required**; reasoning, safety, and autonomous communication matter.

See [Candidate Guide](docs/CANDIDATE_GUIDE.md) for the exact working agreement. The baseline tests are intentionally insufficient to prove correct behavior under concurrent workloads.
