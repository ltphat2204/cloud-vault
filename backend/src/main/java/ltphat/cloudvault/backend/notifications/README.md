# Notifications Module

The Notifications Module is a system-wide service responsible for managing and delivering event-driven alerts to users in CloudVault. It provides a centralized infrastructure for other modules (like Shares) to notify users about relevant activities, ensuring a connected and interactive user experience.

## Features

- **Centralized Alerting**: A unified interface for all backend modules to trigger user notifications.
- **State Tracking**: Persistent management of "read" and "unread" states for every notification.
- **Structured Metadata**: Support for rich metadata (resource IDs, sender info) to enable dynamic client-side rendering.
- **Paginated History**: Optimized retrieval of notification history to ensure performance at scale.
- **Security Scoping**: Strict data isolation ensuring users can only access their own notification data.

## Module Structure

Following Clean Architecture principles:

- **presentation**: REST controllers for listing notifications and marking them as read.
- **application**: Business logic for notification creation, state management, and DTO mapping.
- **domain**: Core aggregate root (`Notification`), `NotificationType` enum, and repository interfaces.
- **infrastructure**: JPA persistence adapters and MapStruct mappers for data conversion.

## Key Components

- `NotificationService`: Primary application service for triggering and managing notifications.
- `NotificationRepository`: Domain interface for notification persistence.
- `NotificationPersistenceAdapter`: Infrastructure bridge to Spring Data JPA.
- `NotificationPersistenceMapper`: MapStruct mapper for domain-persistence conversion.
- `NotificationController`: API entry point at `/api/v1/notifications`.

## Integration Guide

Other modules can trigger user alerts by injecting the `NotificationService` port.

### 1. Inject the Service
Add `NotificationService` to your application service constructor:

```java
private final NotificationService notificationService;
```

### 2. Trigger a Notification
Use the `createNotification` method to dispatch an event:

```java
notificationService.createNotification(
    recipientUserId,
    NotificationType.SHARE_RECEIVED,
    "User A shared a project with you",
    Map.of(
        "resourceId", project.getId().toString(),
        "senderEmail", currentUser.getEmail()
    )
);
```

### 3. Add New Event Types
To support new events, add entries to the `NotificationType` enum in the domain layer.
