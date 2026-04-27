# Table Specification: `projects`

Top-level containers that own folders and files. Supports soft deletion for trash management.

## Schema

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier for the project. |
| `name` | VARCHAR(255) | NOT NULL | Name of the project. |
| `owner_id` | UUID | NOT NULL, FK -> `users(id)` ON DELETE CASCADE | The user who owns this project. |
| `created_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Timestamp when the project was created. |
| `updated_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Timestamp when the project was last updated. |
| `deleted_at` | TIMESTAMPTZ | NULL | Timestamp for soft deletion. If not null, project is in Trash. |

## Indexes

- `idx_projects_owner`: Index on `owner_id`.
- `idx_projects_deleted`: Index on `deleted_at` (for fast trash listing).

## Relationships

- `N:1` with `users` (Owner)
- `1:N` with `folders` (Cascades on permanent delete)
- `1:N` with `files` (Cascades on permanent delete)
- `1:1` polymorphic with `shares`
