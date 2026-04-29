# Audit Module

The Audit Module is a system-wide service responsible for tracking and persisting user activities across the CloudVault platform. It provides a robust audit trail for sensitive operations (upload, download, move, delete, share) and enables users to retrieve the history of specific resources they own or have access to.

## Features

- **Granular Activity Tracking**: Captures 15+ action types including file operations, project management, and sharing events.
- **Flexible Metadata**: Uses PostgreSQL `JSONB` to store action-specific details (e.g., old vs. new file names, sizes, version numbers).
- **Resource-Specific History**: Retrieve the full lifecycle of a Project, Folder, or File.
- **Global Activity Feed**: A centralized view of all actions performed by a user.
- **Security Scoping**: Enforces ownership and permission checks, ensuring history is only visible to authorized users.

## Module Structure

Following Clean Architecture principles:

- **presentation**: REST controllers for listing activities and resource history.
- **application**: Business logic for logging events, permission verification, and DTO mapping.
- **domain**: Core domain model (`ActivityLog`), `ActivityAction` enum, `ResourceType` enum, and repository interfaces.
- **infrastructure**: JPA persistence adapters with JSONB support and MapStruct mappers.

## Key Components

- `IActivityLogService`: Primary application service for logging and querying activities.
- `IActivityLogRepository`: Domain interface for log persistence.
- `ActivityLogPersistenceAdapter`: Infrastructure bridge to Spring Data JPA.
- `ActivityLogController`: API entry point at `/api/v1/audit`.

## Integration Guide

Other modules can log activities by injecting the `IActivityLogService` interface.

### 1. Inject the Service

Add `IActivityLogService` to your application service constructor:

```java
private final IActivityLogService auditService;
```

### 2. Log an Activity

Use the `logActivity` method to record an event. This should typically be done after a successful business operation.

```java
auditService.logActivity(
    userId, 
    ActivityAction.FILE_UPLOADED, 
    ResourceType.FILE, 
    fileId, 
    Map.of("name", fileName, "size", size, "version", version)
);
```

### 3. Supported Actions

The `ActivityAction` enum contains the full list of trackable events. To add new actions, extend the enum in the domain layer.
