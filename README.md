Secure Notes API

A REST API for private notes, built with Spring Boot, Spring Security and JWT. Each user can create, read, update and delete only their own notes. An admin role manages users but cannot read note contents.

Tech stack
Java 21, Spring Boot 4
Spring Security with stateless JWT authentication
Spring Data JPA, Hibernate, MariaDB
JUnit 5 and MockMvc for integration tests (H2 in memory)
How a request flows
MariaDB
Controller and Service
Security rules (SecurityConfig)
JwtAuthenticationFilter
Client (Postman)
MariaDB
Controller and Service
Security rules (SecurityConfig)
JwtAuthenticationFilter
Client (Postman)
alt
[role not allowed for this URL]
[allowed]
alt
[token missing, invalid or expired]
[token valid]
POST /auth/login (username, password)
load user, check BCrypt hash
200 { token }
GET /notes + Authorization: Bearer token
read token, check signature and expiry
continue as anonymous
401 JSON (JwtAuthenticationEntryPoint)
authenticated user with role
403 JSON (JwtAccessDeniedHandler)
call controller
query notes WHERE owner = caller
200 JSON
How the token works
The client logs in with POST /auth/login. The server checks the password against the stored BCrypt hash.
If it matches, the server creates a JWT signed with HS256. The token carries the username (sub), the role, the issue time and an expiry time (tokens last 1 hour).
The client sends the token on every later request in the header Authorization: Bearer <token>.
JwtAuthenticationFilter runs before the controllers. It checks the signature and expiry, loads the user, and tells Spring Security who the caller is.
If the token is missing, wrong or expired, the caller stays anonymous and gets a 401.
The server keeps no session. Everything it needs is in the token.
Roles and access rules
Role	Can do	Cannot do
USER	Create, list, read, update and delete their own notes	Use /admin/**, see other users' notes
ADMIN	Use /admin/** (list users)	Use /notes (gets 403)

Ownership rules:

The note owner is always taken from the token, never from the request body.
Every note lookup is filtered by owner. Asking for someone else's note returns 404, so its existence is not revealed.
Endpoints
Method	Path	Who	Description	Success
POST	/auth/register	Public	Register a new user (always role USER)	201
POST	/auth/login	Public	Log in, returns { "token": "..." }	200
POST	/notes	USER	Create a note	201
GET	/notes	USER	List the caller's notes	200
GET	/notes/{id}	USER	Get one of the caller's notes	200
PUT	/notes/{id}	USER	Update one of the caller's notes	200
DELETE	/notes/{id}	USER	Delete one of the caller's notes	204
GET	/admin/users	ADMIN	List users (no passwords in the response)	200
Error responses

All errors are JSON.

Status	When
400	Validation failed or the JSON body is malformed
401	No token, invalid token or expired token
403	Valid token but the role is not allowed (for example ADMIN on /notes)
404	Note not found, or it belongs to another user

Example:

json
{ "timestamp": "2026-11-02T09:00:00Z", "status": 401, "error": "Unauthorized", "message": "Authentication is required, or the token is missing, invalid or expired", "path": "/notes" }
Running it locally
Create the database:
sql
   CREATE DATABASE noteapp;
Set these environment variables (do not commit real values):
DB_USER
DB_PASSWORD
the JWT secret variable used in application.properties
Start the app:
   ./mvnw spring-boot:run

On Windows: mvnw.cmd spring-boot:run. The API runs on http://localhost:8080.

Creating the first admin

Register a normal user, then promote it in the database:

sql
UPDATE users SET role = 'ADMIN' WHERE username = 'admin';
Example with curl
bash
# log in
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"..."}'

# create a note
curl -X POST http://localhost:8080/notes \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"title":"First note","content":"Hello"}'
Tests
./mvnw test

The integration tests use MockMvc and an in-memory H2 database. They cover login, 401 and 403 responses, role rules, and note ownership (users cannot read, change or delete each other's notes, and the owner cannot be forged).

Project structure
controllers/   REST endpoints (Auth, Note, Admin)
services/      business logic, JwtService, user loading
security/      JWT filter, SecurityConfig, 401 and 403 handlers
models/        JPA entities (Users, Note)
dtos/          request and response objects
exceptions/    custom exceptions and the global handler
repositories/  Spring Data repositories