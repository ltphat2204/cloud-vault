# 4. Technology Stack & Architecture

## 4.1 Technology Stack

**Backend:**

- Java + Spring Boot (core business logic, REST API).
- Spring Security + JWT for authentication and authorization.
- Spring Data JPA + Hibernate for database interaction.
- PostgreSQL as the primary database (stores user, Project, Folder, File metadata, sharing permissions, version history, etc.).
- MinIO as the object storage for actual files (S3-compatible storage).
- Redis for caching (frequently accessed file lists, metadata, sessions, etc.) to improve speed.
- RabbitMQ for Event-Driven Architecture (asynchronous processing of events such as: sending verification and password reset emails).
- SLF4J with Logback for detailed logging.
- Swagger (OpenAPI) for API documentation and testing.
- Unit 5, Mockito, MockMvc for robust TDD and integration testing.

**Frontend:**

- React with Vite (fast build tool).
- TypeScript (strong typing, reduces errors).
- Zustand for global state management (user, current project, etc.).
- Tailwind CSS + shadcn/ui for a beautiful, consistent, and highly customizable UI.
- TanStack Query (React Query) for data fetching, caching, and synchronization.
- TanStack Router for application routing.

**Supporting Tools & DevOps:**

- Docker + Docker Compose to easily run the entire system (Backend + Frontend + PostgreSQL + MinIO + Redis + RabbitMQ).
- Git for source code management.
- Environment variables (.env) for configuration.

## 4.2 Overall Architecture

- **Clean Architecture & Domain-Driven Design (DDD)** on the backend, structured into independent layers:
    - **Domain Layer** – Contains the core business logic, including domain entities (User, Project, Folder, File, Share), value objects, domain services, and repository interfaces. This layer has zero dependencies on frameworks.
    - **Application Layer** – Orchestrates use cases (e.g., UploadFileUseCase, ShareProjectUseCase) and defines event interfaces. It coordinates domain objects and infrastructure to fulfill user intents.
    - **Infrastructure Layer** – Implements repository contracts, external service adapters (MinIO, RabbitMQ, Redis), event dispatching, and persistence details (Spring Data JPA, Hibernate). This layer bridges the domain to the outside world.
    - **Interface / Presentation Layer** – REST controllers, WebSocket endpoints, and any external APIs that translate HTTP and real-time requests into application use cases.
- **Clear separation of concerns**: Application metadata lives in PostgreSQL (cached with Redis when appropriate); binary file content is stored in MinIO (S3-compatible object storage).
- **Event-Driven**: Authentication events (RegistrationCompleted, PasswordResetRequested) are published to RabbitMQ. Dedicated consumers in the infrastructure layer handle asynchronous email delivery without blocking the request cycle.
- **Real-time**: Notifications and upload progress are pushed to the frontend via WebSocket, keeping the UI instantly synced with backend changes.
- **Streaming**: Large downloads (e.g., folder ZIP archives) are streamed directly to the client using `StreamingResponseBody`, minimizing memory pressure on the server.

## 4.3 Navigation & Pagination

- **Cursor-Based Pagination**: The system uses keyset (cursor) pagination for all listing endpoints (Files, Folders, Audit Logs). This approach ensures stable paging even as new data is added, improves performance on large datasets, and enables seamless infinite scrolling in the UI.
- **Sorting**: Sorting is supported on multiple fields (e.g., name, size, createdAt) and is integrated with the cursor generation logic to maintain consistency.
- **Base64 Encoding**: Cursors are Base64 encoded to abstract internal database fields (timestamps, IDs) from the client.