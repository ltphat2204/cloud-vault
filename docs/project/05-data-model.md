# 5. Main Data Structure (Database Model – Summary)

CloudVault uses a relational database model (PostgreSQL) to manage users, projects, folders, and files. The system supports versioning, soft deletion, and polymorphic sharing (including public links).

## High-Level Entities

- **Users:** System users and their authentication data.
- **Projects:** Top-level containers for all folders and files. Supports soft delete.
- **Folders:** Hierarchical organization within projects. Supports soft delete.
- **Files:** Data objects stored in MinIO with version history. Supports soft delete.
- **Shares:** Polymorphic access control for projects, folders, and files. Supports internal user sharing and **public link tokens**.
- **Notifications:** User alerts for system events.
- **Activity Logs:** Audit trail of user actions.

## Detailed Schema Documentation

For a complete breakdown of the database schema, including individual table specifications, column types, constraints, and the Entity-Relationship Diagram (ERD), please refer to the:

**[Detailed Database Schema Design](../designs/schemas/README.md)**

## Core Features Supported

- **UUIDs:** All entities use UUIDs for security and scalability.
- **Soft Delete:** Projects, Folders, and Files support soft deletion via `deleted_at`.
- **Versioning:** Files maintain a history of versions in a dedicated table.
- **Polymorphic sharing**: A single mechanism handles internal sharing and public link generation (with optional password protection).