# Table Specification: `activity_logs`

Audit trail for tracking user actions within the system.

## Schema

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier for the log entry. |
| `user_id` | UUID | NOT NULL, FK -> `users(id)` ON DELETE CASCADE | The user who performed the action. |
| `action` | VARCHAR(100) | NOT NULL | Action name (e.g., 'FILE_UPLOADED'). |
| `resource_type` | VARCHAR(20) | NULL | Type of affected resource. |
| `resource_id` | UUID | NULL | ID of the affected resource. |
| `details` | JSONB | NULL | Additional metadata about the action. |
| `created_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Log entry time. |

## Indexes

- `idx_activity_user_created`: Index on `(user_id, created_at DESC)`.
- `idx_activity_resource`: Index on `(resource_type, resource_id)`.

## Relationships

- `N:1` with `users` (Actor)
