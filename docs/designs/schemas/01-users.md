# Table Specification: `users`

Stores system user accounts and authentication data.

## Schema

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier for the user. |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | User email address (used for login). |
| `password_hash` | VARCHAR(255) | NOT NULL | Hashed password. |
| `name` | VARCHAR(255) | NOT NULL | Full name or display name. |
| `created_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Timestamp when the user was created. |
| `updated_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Timestamp when the user was last updated. |

## Indexes

- `idx_users_email`: UNIQUE index on `email`.

## Relationships

- `1:N` with `projects` (Owner)
- `1:N` with `folders` (Owner)
- `1:N` with `files` (Owner)
- `1:N` with `shares` (Recipient)
- `1:N` with `notifications` (Recipient)
- `1:N` with `activity_logs` (Actor)
