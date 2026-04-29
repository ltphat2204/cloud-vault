# Test Cases

This directory contains detailed test cases for the CloudVault system, organized by module. These test cases cover functional requirements, security constraints, and edge cases.

## Modules

- **[01-IAM](01-iam.md)**: Authentication, registration, token management, and security context.
- **[02-Projects](02-projects.md)**: Project lifecycle, ownership enforcement, and soft deletion.
- **[03-Folders](03-folders.md)**: Hierarchical organization, folder movement, circular reference validation, and recursive deletion.
- **[04-Files](04-files.md)**: File metadata management, versioning, movement, and soft deletion.
- **[05-Trash](05-trash.md)**: Centralized management of deleted items, batch restoration, and permanent removal.
- **[06-Shares](06-shares.md)**: Internal resource sharing, permission management, and public link access.
- **[07-Notifications](07-notifications.md)**: Event-driven alerts, real-time WebSocket delivery, and read state management.
- **[08-Audit](08-audit.md)**: Activity logging, system events tracking, and compliance monitoring.
- **[09-Search](09-search.md)**: Global and scoped resource discovery across projects.

## Test Strategy

CloudVault uses a multi-layered testing strategy:
- **Unit Tests**: Business logic in services and domain models (JUnit 5 + Mockito).
- **Web Layer Tests**: REST API contracts and input validation (MockMvc).
- **Integration Tests**: End-to-end flows with real infrastructure (Testcontainers + PostgreSQL/Redis).

## Naming Convention
Each file is prefixed with a number representing its logical order or priority in the system architecture.
- `0x-module.md`: Functional test cases for a specific module.
