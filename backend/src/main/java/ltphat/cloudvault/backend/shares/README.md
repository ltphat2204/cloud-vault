# Shares Module

The `shares` module manages resource sharing permissions for Projects, Folders, and Files. It supports both internal sharing with registered users and public link sharing with optional password protection and expiration.

## Features

- **Internal Sharing**: Grant VIEW or EDIT permissions to other users via email.
- **Public Link Sharing**: Generate unique access tokens for resources.
- **Password Protection**: Secure public links with BCrypt-hashed passwords.
- **Expiration**: Set expiry dates for public links.
- **Shared With Me**: List resources shared with the authenticated user, sorted by date descending.
- **Resource Ownership Validation**: Ensures only owners can share or manage shares for their resources.

## Architecture

Following Clean Architecture principles:

- **Domain**: ResourceType and Permission enums, Share domain model, and repository interface.
- **Application**: ShareService for orchestrating use cases and DTOs for API communication.
- **Infrastructure**: JPA entities and persistence adapters.
- **Presentation**: REST controllers for sharing management.

## API Specification

Refer to the global [API Specification](../../../../../../../../docs/designs/api-specification/06-shares.md) for detailed endpoint documentation.

## Database Schema

The module uses the `shares` table. Refer to [Schema Design](../../../../../../../../docs/designs/schemas/06-shares.md).
