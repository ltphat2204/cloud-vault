# Table Specification: `files`

Metadata and current state for files stored in the system.

## Schema

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier for the file. |
| `name` | VARCHAR(255) | NOT NULL | Display name of the file. |
| `minio_key` | VARCHAR(500) | NOT NULL | Storage path in MinIO for the current version. |
| `size` | BIGINT | NOT NULL, DEFAULT 0 | Size in bytes for the current version. |
| `mime_type` | VARCHAR(127) | NULL | MIME type of the file. |
| `folder_id` | UUID | NULL, FK -> `folders(id)` ON DELETE SET NULL | Parent folder (NULL if at project root). |
| `project_id` | UUID | NOT NULL, FK -> `projects(id)` ON DELETE CASCADE | Project ownership. |
| `owner_id` | UUID | NOT NULL, FK -> `users(id)` ON DELETE CASCADE | User ownership. |
| `version_number` | INT | NOT NULL, DEFAULT 1 | Current active version number. |
| `current_version_id` | UUID | NULL | Shortcut link to the latest `file_versions` record. |
| `created_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Timestamp when file entry was created. |
| `updated_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Last modification timestamp. |
| `deleted_at` | TIMESTAMPTZ | NULL | Timestamp for soft deletion. |

## Indexes

- `idx_files_project_folder`: Index on `(project_id, folder_id)`.
- `idx_files_owner`: Index on `owner_id`.
- `idx_files_name_search`: Index on `name` (B-tree or GIN).

## Relationships

- `N:1` with `folders`
- `N:1` with `projects`
- `N:1` with `users` (Owner)
- `1:N` with `file_versions` (History)
- `1:1` polymorphic with `shares`
