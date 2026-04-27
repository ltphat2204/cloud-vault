# Table Specification: `notifications`

Stores user notifications for system events.

## Schema

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier for the notification. |
| `user_id` | UUID | NOT NULL, FK -> `users(id)` ON DELETE CASCADE | Recipient user. |
| `type` | VARCHAR(50) | NOT NULL | Event type (e.g., 'SHARE_RECEIVED'). |
| `message` | TEXT | NOT NULL | Human-readable notification message. |
| `is_read` | BOOLEAN | NOT NULL, DEFAULT FALSE | Read status. |
| `metadata` | JSONB | NULL | Flexible storage for extra context (resource IDs, etc.). |
| `created_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Notification delivery time. |

## Indexes

- `idx_notifications_user_unread`: Index on `(user_id, is_read)` where `is_read = FALSE`.
- `idx_notifications_user_created`: Index on `(user_id, created_at DESC)`.

## Relationships

- `N:1` with `users` (Recipient)
