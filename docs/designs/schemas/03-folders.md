# Table Specification: `folders`

Hierarchical organization units within projects.

## Schema

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier for the folder. |
| `name` | VARCHAR(255) | NOT NULL | Name of the folder. |
| `parent_folder_id` | UUID | NULL, FK -> `folders(id)` ON DELETE SET NULL | Parent folder for hierarchical structure. |
| `project_id` | UUID | NOT NULL, FK -> `projects(id)` ON DELETE CASCADE | The project this folder belongs to. |
| `owner_id` | UUID | NOT NULL, FK -> `users(id)` ON DELETE CASCADE | The user who owns this folder. |
| `created_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Timestamp when the folder was created. |
| `updated_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Timestamp when the folder was last updated. |
| `deleted_at` | TIMESTAMPTZ | NULL | Timestamp for soft deletion. |

## Indexes

- `idx_folders_project_parent`: Index on `(project_id, parent_folder_id)`.
- `idx_folders_owner`: Index on `owner_id`.

## Relationships

- `N:1` with `projects`
- `N:1` with `users` (Owner)
- `N:1` self-reference with `folders` (Parent)
- `1:N` self-reference with `folders` (Children)
- `1:N` with `files`
- `1:1` polymorphic with `shares`
