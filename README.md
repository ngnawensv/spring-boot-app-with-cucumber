# Spring Boot App with Cucumber

A small Spring Boot REST example that manages users (create, read, update, delete, deactivate) with validation, error handling, and Cucumber BDD integration tests.

## Features

- REST API for user management under `/api/users`
- JPA `User` entity with validation: `name` (@NotBlank), `email` (@NotBlank, @Email, unique), `active` flag
- Service layer with transactional business rules (unique email check, soft deactivate)
- Global exception handling (`GlobalExceptionHandler`) returning clear HTTP error responses
- Cucumber BDD scenarios for end-to-end tests (`src/test/resources/features/user-management.feature`)

## Tech stack

- Java 21
- Spring Boot 4.x
- Spring Data JPA
- Jakarta Bean Validation
- H2 (in-memory for tests)
- Cucumber (BDD tests)
- Lombok
- Gradle (wrapper included)

## Quick start (Windows PowerShell)

Build the project:

```powershell
.\gradlew.bat build
```

Run the application locally:

```powershell
.\gradlew.bat bootRun
```

Run the test suite (including Cucumber):

```powershell
.\gradlew.bat test
```

Cucumber reports will be generated under `build/reports/cucumber/`.

## API summary

Base path: `/api/users`

- POST /api/users — create user (201 Created, or 400 Bad Request on validation/duplicate email)
- GET /api/users/{id} — get user by id (200 OK, or 404 Not Found)
- GET /api/users — list all users (200 OK)
- PUT /api/users/{id} — update user (200 OK, or 404 Not Found)
- DELETE /api/users/{id} — delete user (204 No Content, or 404 Not Found)
- PATCH /api/users/{id}/deactivate — deactivate user (200 OK, or 404 Not Found)

Request/response objects currently reuse the JPA `User` entity (consider adding DTOs for production).

## Tests and BDD

- Feature files: `src/test/resources/features/user-management.feature`
- Cucumber runner: `src/test/java/cm/belrose/cucumber/TestCucumberRunner.java`
- Spring integration config for Cucumber: `src/test/java/cm/belrose/cucumber/CucumberSpringConfiguration.java`

## Notes & suggestions

- The `User` entity is used directly as API DTO; for larger projects introduce separate DTOs and mapping.
- The service enforces email uniqueness and the DB has a unique constraint; to fully handle race conditions consider catching `DataIntegrityViolationException` and returning a friendly 400 response.
- Add pagination to `GET /api/users` if the dataset grows.

## Where to look in the code

- `src/main/java/cm/belrose/controller/UserController.java`
- `src/main/java/cm/belrose/service/UserService.java`
- `src/main/java/cm/belrose/repository/UserRepository.java`
- `src/main/java/cm/belrose/model/User.java`
- `src/main/java/cm/belrose/exception/GlobalExceptionHandler.java`
- `src/test/resources/features/user-management.feature`

---

If you'd like, I can:
- Open a PR adding `README.md` to the repo (if desired workflow)
- Generate an OpenAPI spec for the API
- Add a small improvement (e.g., handle `DataIntegrityViolationException`) and run tests
