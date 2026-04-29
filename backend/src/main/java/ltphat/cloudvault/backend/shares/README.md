# Shares Module

The Shares Module manages resource sharing permissions for Projects, Folders, and Files in CloudVault. It supports both internal sharing with registered users and public link sharing with optional password protection and expiration, ensuring flexible and secure collaboration.

## Features

- **Internal Sharing**: Grant `VIEW` or `EDIT` permissions to registered users via email.
- **Public Link Sharing**: Generate unique, shareable access tokens for resources with optional security controls.
- **Security Controls**: Protect public links with BCrypt-hashed passwords and set expiration dates.
- **Shared With Me**: Efficiently list resources shared with the authenticated user, sorted by date.
- **Ownership Validation**: Strict security checks to ensure only resource owners can manage sharing permissions.
- **Event-Driven Notifications**: Automatically integrates with the Notifications module to alert recipients of sharing activities.

## Module Structure

Following Clean Architecture principles:

- **presentation**: REST controllers for managing shares, public access, and "Shared with me" listings.
- **application**: Business logic for permission management, token generation, and coordination with the Notifications module.
- **domain**: Core entities (`Share`), enums (`Permission`, `ResourceType`), and repository interfaces.
- **infrastructure**: JPA persistence adapters and entity mappings for share records.

## Key Components

- `IShareService`: Core application service orchestrating the sharing lifecycle.
- `ShareRepository`: Domain interface for share metadata persistence.
- `ShareController`: Presentation layer handling API requests at `/api/v1/shares`.
- `PublicShareController`: Specialized controller for password-protected public link access.
- `NotificationService`: Integrated external service for triggering user alerts on sharing events.

## Notification Integration

The Shares module triggers automated notifications for the following lifecycle events:

- **`SHARE_RECEIVED`**: Dispatched when a new resource (Project, Folder, or File) is shared with a recipient.
- **`SHARE_UPDATED`**: Dispatched when an existing share's permission level (e.g., VIEW to EDIT) is modified.
- **`SHARE_REVOKED`**: Dispatched when a recipient's access to a resource is removed.

These notifications include metadata such as the `resourceId` and `senderEmail` to provide context for the recipient.
