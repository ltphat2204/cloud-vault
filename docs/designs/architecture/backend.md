# Backend Architecture – Detailed Description

## Technology Stack
- **Java 21**, **Spring Boot 3**
- **Spring Security** with JWT (access + refresh tokens)
- **Spring Data JPA / Hibernate** (database access)
- **PostgreSQL** – primary metadata store
- **MinIO** – S3‑compatible object store for files
- **Redis** – cache and token blacklist/refresh token store
- **RabbitMQ** – message broker for domain events
- **SLF4J + Logback** – logging and observability
- **Springdoc OpenAPI / Swagger UI** – API documentation and discovery
- **JUnit 5, Mockito, MockMvc** – testing suite

## Clean Architecture Layers

### 1. Domain Layer
Pure business logic.  
Contains:
- Entities: `User`, `Project`, `Folder`, `File`, `Share`, `FileVersion`, `Notification`
- Value Objects: `Email`, `StoragePath`, `Permission`
- Domain Services: `FileOwnershipService`, `FolderHierarchyValidator`
- Repository Interfaces (technology‑agnostic)
- Domain Events: `FileUploadedEvent`, `ProjectSharedEvent`, etc.

Zero framework dependencies.

### 2. Application Layer
Orchestrates use cases.  
Contains:
- Use Case classes: `UploadFileUseCase`, `ShareProjectUseCase`, `CreateFolderUseCase`, etc.
- Command/Query DTOs
- Event Bus Interface (implemented by infrastructure)

### 3. Infrastructure Layer
Technical implementations:
- **Persistence**: JPA repositories, entity mappings
- **Object Storage**: `MinioFileStorageAdapter` implementing `FileStoragePort`
- **Messaging**: `RabbitMqEventPublisher`, `RabbitMqListener` (consumers)
- **Caching**: `RedisCacheService` wrapping frequently accessed metadata
- **Security**: JWT authentication filter, token refresh logic
- **Real‑time**: WebSocket sessions managed via a `NotificationSocketService`

### 4. Interface / Presentation Layer
- REST Controllers (authenticated endpoints)
- WebSocket endpoints for real‑time updates
- Global exception handler, DTO validation
- Swagger / OpenAPI 3 UI for API discovery and testing

## Request Lifecycle Example (File Upload)
1. Client POSTs to `/api/files/upload` with multipart data and upload‑progress through Socket.
2. Controller → `UploadFileUseCase.execute(command)`.
3. Use case validates project/folder ownership, checks storage quota.
4. Calls `FileStoragePort.store(file)` → MinIO.
5. Saves file metadata via `FileRepository`.
6. Publishes `FileUploadedEvent` to RabbitMQ.
7. Returns file metadata; WebSocket channel pushes progress to client.

## Event-Driven Processing
After domain events are published, infrastructure consumers handle:
- **Thumbnail generation** (for images/videos) – worker service
- **Search index update** (future Elasticsearch or PostgreSQL full‑text)
- **Notification creation & push** – saves notification to DB and emits to recipient’s WebSocket connection.

## Security
- All endpoints require JWT access token (Bearer).
- Role‑based access: owners, shared recipients with VIEW/EDIT.
- File type validation, size limits configured per environment.

## Testing Strategy
The project follows a Test-Driven Development (TDD) approach with a goal of 80%+ coverage:
- **Unit Testing**: JUnit 5 + Mockito for isolated business logic testing in the Domain and Application layers.
- **Web Layer Testing**: MockMvc for testing REST controllers and security rules without full server startup.
- **Integration Testing**: SpringBootTest + Testcontainers for full end-to-end flow validation with real infrastructure (PostgreSQL, Redis, MinIO).
