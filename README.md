# Secure Notes App with JWT

A Spring Boot REST API where people keep private notes. Users register, log in to receive a JWT, and send that token with every request. Each note belongs to one user, and nobody else can read or change it.

**Project 2** · Mon 26 Oct 2026 to Sun 08 Nov 2026 · 14 days · 11 milestones · 54 activities · 26 hours

**Goal:** I can explain authentication vs authorization in my own code, and user A cannot read user B's note.

- **Authentication** answers "who are you?": register, login with username and password, receive a signed token.
- **Authorization** answers "what may you do?": the token carries a role (`USER` or `ADMIN`), and every note is checked against its owner.

## Tech stack

- Java 21, Spring Boot 4.1
- Spring Security (stateless, JWT)
- Spring Data JPA + MariaDB
- jjwt for signing and reading tokens
- Lombok, Jakarta Validation

## Getting started

1. **Database:** create a MariaDB database named `noteapp` and set the URL, username and password in `src/main/resources/application.properties`.
2. **Secret key:** generate a Base64 secret of at least 32 bytes and expose it as an environment variable. Never commit it.

   ```powershell
   $b = New-Object byte[] 32; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b)
   ```

   Then set `JWT_SECRET=<that value>` in your run configuration or shell.
3. **Properties** used by the app:

   ```
   jwt.secret=${JWT_SECRET}
   jwt.expiration-ms=3600000
   ```
4. **Run:**

   ```
   ./mvnw spring-boot:run
   ```

   The API starts on `http://localhost:8080`.

## Endpoints

| Method | Path | Access | Purpose |
|---|---|---|---|
| POST | `/auth/register` | Public | Create an account (role is always `USER`) |
| POST | `/auth/login` | Public | Log in and receive a JWT |

The full table, including notes and admin endpoints, is in [`docs/endpoints.md`](docs/endpoints.md).

Using the token on a protected request:

```
Authorization: Bearer <token>
```

## Design documents

| Document | File |
|---|---|
| Requirements | [`docs/requirements.md`](docs/requirements.md) |
| Entity diagram | [`docs/entity-diagram.png`](docs/entity-diagram.png) |
| Authentication flow diagram | [`docs/auth-flow-diagram.png`](docs/auth-flow-diagram.png) |
| Endpoints and required access | [`docs/endpoints.md`](docs/endpoints.md) |

## What must never leak

1. Raw passwords: never stored, logged or returned. Only BCrypt hashes are kept.
2. The password hash: never in any response (responses use DTOs).
3. Other users' notes: every note query is checked against the logged-in owner.
4. The JWT secret: kept in an environment variable, never committed.
5. Login hints: a failed login returns one generic message, whichever part was wrong.
6. Internals: no stack traces or SQL errors in responses.

## Project plan

**Mon 26 Oct 2026 to Sun 08 Nov 2026** · 14 days · 11 milestones · 54 activities · 26 hours

| # | Milestone | Dates | Activities | Hours | Done when | Status |
|---|---|---|---|---|---|---|
| 1 | Design on paper | Mon 26 Oct | 4 | 2 | Entity diagram, token flow diagram and endpoint table saved in the repo | Done |
| 2 | Project setup | Tue 27 Oct | 5 | 2 | App starts, connects to the database, first commit is on GitHub, User entity ready | Done |
| 3 | Registration with BCrypt | Wed 28 Oct | 6 | 2 | Passwords are stored as hashes; duplicate usernames are rejected with 409 | Done |
| 4 | Login and JWT | Thu 29 Oct | 4 | 2 | Login returns a token with an expiry; wrong credentials return 401 | In progress |
| 5 | JWT filter | Fri 30 Oct - Sat 31 Oct | 8 | 4 | A valid token reaches protected endpoints; a missing, bad or expired token is blocked | To do |
| 6 | Notes CRUD | Mon 02 Nov - Tue 03 Nov | 9 | 4 | Create, list, read, update and delete work for a logged-in user | To do |
| 7 | Ownership rule | Wed 04 Nov | 4 | 2 | A user sees and changes only their own notes | To do |
| 8 | Custom 401 and 403 handlers | Thu 05 Nov | 4 | 2 | Not logged in returns 401 JSON; not allowed returns 403 JSON | To do |
| 9 | Admin role | Fri 06 Nov | 4 | 2 | Admin lists users (no passwords) but cannot read note contents | To do |
| 10 | Attack tests and documentation | Sat 07 Nov | 4 | 2 | User A cannot read user B's note; tests pass; README written; v1.0 tagged | To do |
| R | Weekly review | Sun 01 Nov - Sun 08 Nov | 2 | 2 | Notes updated, open questions listed, final done-when check run | To do |

**Totals:** 54 activities, 26 hours.

## Final check (end of project)

- [ ] I can explain authentication vs authorization using my own code.
- [ ] User A cannot read, change or delete user B's note (tested).
- [ ] Duplicate usernames return 409.
- [ ] Wrong credentials return 401 with one generic message.
- [ ] A missing, bad or expired token is blocked, with a JSON 401 or 403.
- [ ] Admin lists users without passwords and cannot read note contents.
- [ ] Tests pass and `v1.0` is tagged.
