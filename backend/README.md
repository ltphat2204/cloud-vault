# CloudVault Backend

This is the core backend service for CloudVault, built with Java 21, Spring Boot 3, and PostgreSQL. It follows **Clean Architecture** principles and **Test-Driven Development (TDD)** patterns.

## Modules Summary

The system is decomposed into highly cohesive modules, each following a strict layered architecture:

- [**`iam`**](src/main/java/ltphat/cloudvault/backend/iam/README.md): Identity and Access Management. Handles user registration, authentication (JWT), and security context.
- [**`projects`**](src/main/java/ltphat/cloudvault/backend/projects/README.md): Top-level containers for organization. Manages project lifecycle and ownership.
- [**`folders`**](src/main/java/ltphat/cloudvault/backend/folders/README.md): Hierarchical directory structure within projects. Supports recursive operations and soft deletion.
- [**`files`**](src/main/java/ltphat/cloudvault/backend/files/README.md): Files management with versioning support and MinIO-backed storage.
- [**`shares`**](src/main/java/ltphat/cloudvault/backend/shares/README.md): Resource sharing module. Enables internal user-to-user sharing and secure public link sharing.
- [**`notifications`**](src/main/java/ltphat/cloudvault/backend/notifications/README.md): System-wide notification delivery and management.
- [**`trash`**](src/main/java/ltphat/cloudvault/backend/trash/README.md): Centralized management for soft-deleted resources across all modules.
- [**`shared`**](src/main/java/ltphat/cloudvault/backend/shared/README.md): Cross-cutting concerns, including global security configuration, common DTOs, and exception handlers.

## Architecture

We follow **Clean Architecture** to ensure the business logic is independent of frameworks, UI, and database:

1.  **Domain Layer** (`domain`):
    - Contains business entities, value objects, and enums.
    - Defines repository interfaces (ports).
    - Contains core business logic and exceptions.
2.  **Application Layer** (`application`):
    - Orchestrates use cases.
    - Contains service implementations.
    - Defines Data Transfer Objects (DTOs).
3.  **Infrastructure Layer** (`infrastructure`):
    - Implements repository interfaces using JPA/Spring Data.
    - Handles persistence mapping (Domain Entity <-> JPA Entity).
    - Integrates with external services (e.g., MinIO).
4.  **Interface Layer** (`presentation`):
    - Exposes REST endpoints via Controllers.
    - Handles request validation and security principal injection.

## Implementation Workflow

To implement a new feature or module (e.g., the `shares` module), follow this systematic process:

### 1. Design & Documentation
- **API Specification**: Create a markdown file in `docs/designs/api-specification/` defining endpoints, request/response formats, and status codes.
- **Test Cases**: Define scenarios in `docs/test-cases/` covering success paths, edge cases, and security requirements.
- **Schema**: Define the database schema in `docs/designs/schemas/`.

### 2. Domain Layer Implementation
- Define core entities and enums in the `domain.model` package.
- Define the repository interface in `domain.repository`.
- Define module-specific exceptions in `domain.exception`.

### 3. Infrastructure Layer Implementation
- Create the JPA entity in `infrastructure.persistence.jpa`.
- Implement the Spring Data repository.
- Implement a `PersistenceMapper` and `PersistenceAdapter` to translate between Domain and JPA layers.

### 4. Application Layer & TDD
- Define DTOs for the API.
- Define the Service interface.
- Implement the Service using TDD:
    - Write unit tests in `src/test/java/.../application/service`.
    - Implement logic to pass tests (e.g., ownership validation, business rules).

### 5. Interface Layer & Integration
- Create the REST Controller in `presentation.controller`.
- Integrate with `GlobalExceptionHandler` to map business exceptions to HTTP codes.
- Update `SecurityConfig` if public access or specific roles are required.

### 6. Comprehensive Testing
- **Controller Tests**: WebMvcTests in `presentation/controller` to verify routing and security.
- **Integration Tests**: SpringBootTests in `integration/` using Testcontainers (PostgreSQL/MinIO) to verify end-to-end flows.

## Best Practices
- **Immutability**: Prefer immutable domain models and DTOs (use `@Builder`).
- **Validation**: Always validate at system boundaries (Controllers).
- **Security**: Use `@AuthenticationPrincipal UserPrincipal` to access the current user safely.
- **Clean Code**: Keep functions small, files focused (< 800 lines), and naming descriptive.
