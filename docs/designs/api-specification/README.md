# CloudVault – API Specification Agenda

This directory contains the detailed API specifications for each module in the CloudVault backend.

## Design Principles

- **RESTful Architecture**: All APIs follow standard REST patterns.
- **Security**: Authentication is handled via JWT (Access Token in header, Refresh Token in secure HttpOnly cookie).
- **Standard Response**: All responses are wrapped in a consistent `ApiResponse<T>` structure.
- **Status Codes**: 
    - `200 OK`: Success.
    - `201 Created`: Resource successfully created.
    - `400 Bad Request`: Invalid input or business logic violation.
    - `401 Unauthorized`: Missing or invalid credentials.
    - `403 Forbidden`: Insufficient permissions.
    - `404 Not Found`: Resource does not exist.
    - `500 Internal Server Error`: Unexpected system failure.

## Standard Response Wrapper

Every API response follows this schema:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2024-04-28T10:00:00Z"
}
```

## Module Specifications

| Module | Description | File |
| --- | --- | --- |
| **IAM** | Identity and Access Management (Auth) | [01-iam.md](./01-iam.md) |
| **Projects** | Project Management | [02-projects.md](./02-projects.md) |
| **Folders** | Folder Hierarchy Management | [03-folders.md](./03-folders.md) |
| **Files** | File and Version Management | [04-files.md](./04-files.md) |
| **Trash** | Deleted Items Management | [05-trash.md](./05-trash.md) |
| **Shares** | Resource Sharing & Permissions | [06-shares.md](./06-shares.md) |
| **Notifications** | Real-time & Persistent Alerts | [07-notifications.md](./07-notifications.md) |
| **Audit** | Activity & System Logging | [08-audit.md](./08-audit.md) |
| **Search** | Global & Scoped Resource Discovery | [09-search.md](./09-search.md) |
