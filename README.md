# BankCore

![CI](https://github.com/Bodepk/BankCore/actions/workflows/ci.yml/badge.svg)

A REST API for a banking system, built with **Spring Boot 3** and **Spring Security**. It handles user authentication with JWT, bank account management, and money operations (deposits, withdrawals and transfers) with ownership-based and role-based authorization.

This project was built as a portfolio piece to practice real-world backend concerns: authentication, authorization, transactional integrity, layered architecture, and automated testing — not just CRUD endpoints.

## Features

- **Authentication** — register, login, JWT access/refresh tokens, and a `/me` endpoint that returns the authenticated user's own data.
- **Account ownership** — every account belongs to a user. A regular `USER` can only operate on their own accounts; an `ADMIN` can operate on any account.
- **Money operations** — deposit, withdraw, and transfer between accounts, with balance validation and full transaction history.
- **Transaction insights** — filter transactions by date range, fetch recent transactions, and get an account summary (totals by type).
- **Consistent error handling** — a global exception handler that returns clean, predictable JSON for validation errors, insufficient balance, authentication failures (401) and authorization failures (403), instead of leaking stack traces.
- **API documentation** — interactive Swagger UI with JWT bearer auth support, so every endpoint can be explored and tested from the browser.
- **Automated tests** — unit tests for the account/transaction business logic (ownership rules, balance checks, transfers) and integration tests for the full authentication flow, running against an in-memory H2 database.
- **CI** — every push runs the full test suite automatically via GitHub Actions.

## Tech stack

- **Java 21**, **Spring Boot 3.5**
- **Spring Security** with JWT (stateless, no sessions)
- **Spring Data JPA** + **PostgreSQL** (production/dev), **H2** (tests)
- **Flyway** for database migrations
- **Gradle**
- **JUnit 5**, **Mockito**, **MockMvc**
- **springdoc-openapi** (Swagger UI)
- **Docker Compose** (local PostgreSQL)

## Getting started

### Prerequisites

- JDK 21+
- Docker (for the local PostgreSQL instance)

### Run it

```bash
# 1. Start the database
docker-compose up -d

# 2. Run the app (migrations run automatically on startup)
./gradlew bootRun
```

The API will be available at `http://localhost:8080`, and the interactive documentation at:

```
http://localhost:8080/swagger-ui.html
```

By default the app runs with sensible local-dev fallback values (see `application.yml` / `application-dev.yml`), so the two commands above are enough to get it running. For a real deployment, override the JWT secret and database credentials with environment variables — see `.env.example`.

### Run the tests

```bash
./gradlew test
```

Tests run against an in-memory H2 database, so no external services are required.

## Quick API tour

| Method | Endpoint | Description | Auth required |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Register a new user | No |
| POST | `/api/v1/auth/login` | Log in, get access + refresh tokens | No |
| POST | `/api/v1/auth/refresh` | Get a new token pair | No |
| GET | `/api/v1/auth/me` | Get the authenticated user's own data | Yes |
| POST | `/api/v1/accounts` | Create an account (owned by the caller) | Yes |
| GET | `/api/v1/accounts/{accountNumber}` | Get an account (owner or admin only) | Yes |
| POST | `/api/v1/accounts/{accountNumber}/deposit` | Deposit money | Yes |
| POST | `/api/v1/accounts/{accountNumber}/withdraw` | Withdraw money | Yes |
| POST | `/api/v1/accounts/transfer` | Transfer between two accounts | Yes |
| GET | `/api/v1/accounts/{accountNumber}/transactions` | Transaction history | Yes |
| GET | `/api/v1/accounts/{accountNumber}/summary` | Transaction summary | Yes |
| POST | `/api/v1/accounts/{accountNumber}/block` | Block an account | Admin only |

The full, always-up-to-date list — including request/response schemas — is in Swagger UI.

## Design notes

A few decisions worth calling out, since they're the kind of thing that tends to come up in a code review:

- **401 vs 403** — the API distinguishes "you're not authenticated" (401) from "you're authenticated but not allowed to do this" (403), instead of collapsing both into one status code.
- **Ownership checks live in the service layer**, not the controller, so they can't be bypassed by adding a new endpoint that forgets to check.
- **Transfers only validate ownership of the source account** — you can send money to someone else's account (as in real banking), but you can't withdraw from an account that isn't yours (unless you're an admin).

## Author

**Bode** — Computer Engineering student, backend development (Java / Spring Boot)

- Email: bode950929@gmail.com
- WhatsApp: +53 5116 1636
- LinkedIn: [linkedin.com/in/josé-antonio-herrera-aviles-849843326](https://www.linkedin.com/in/josé-antonio-herrera-aviles-849843326)
- GitHub: [@Bodepk](https://github.com/Bodepk)
