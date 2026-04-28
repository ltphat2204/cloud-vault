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
| **IAM** | Identity and Access Management (Auth) | [iam.md](./iam.md) |
| **Projects** | Project Management | [projects.md](./projects.md) |
