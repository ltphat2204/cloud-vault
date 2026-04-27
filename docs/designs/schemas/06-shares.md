# Table Specification: `shares`

Manages sharing permissions for Projects, Folders, and Files. Supports both internal sharing (with registered users) and public link sharing.

## Schema

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier for the share record. |
| `resource_type` | VARCHAR(20) | NOT NULL, CHECK (resource_type IN ('PROJECT','FOLDER','FILE')) | The type of resource being shared. |
| `resource_id` | UUID | NOT NULL | ID of the Project, Folder, or File. |
| `shared_with_user_id` | UUID | **NULL**, FK -> `users(id)` ON DELETE CASCADE | The recipient user (NULL for public links). |
| `permission` | VARCHAR(10) | NOT NULL, CHECK (permission IN ('VIEW','EDIT')) | Access level. Public links are typically 'VIEW'. |
| `access_token` | UUID | **NULL**, UNIQUE | Unique token for public link access (UC-17). |
| `password_hash` | VARCHAR(255) | **NULL** | Optional password hash for public links (UC-17). |
| `expires_at` | TIMESTAMPTZ | NULL | Optional expiration date for the share. |
| `created_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Share creation time. |
| `updated_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Last update time. |

## Indexes

- `idx_shares_shared_with`: Index on `shared_with_user_id`.
- `idx_shares_resource`: Index on `(resource_type, resource_id)`.
- `idx_shares_token`: Index on `access_token` (for fast lookups via public URL).
- Unique constraint: `(resource_type, resource_id, shared_with_user_id)` where `shared_with_user_id` is NOT NULL.

## Sharing Modes

1.  **Internal Share**: `shared_with_user_id` is NOT NULL, `access_token` is NULL.
2.  **Public Link**: `shared_with_user_id` is NULL, `access_token` is NOT NULL.
3.  **Password-Protected Public Link**: Same as public link, but `password_hash` is NOT NULL.

## Relationships

- `N:1` with `users` (Recipient)
- Polymorphic relationship with `projects`, `folders`, or `files`.
