# Table Specification: `file_versions`

Stores the history of all file versions.

## Schema

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | UUID | PRIMARY KEY, DEFAULT gen_random_uuid() | Unique identifier for this version. |
| `file_id` | UUID | NOT NULL, FK -> `files(id)` ON DELETE CASCADE | Reference to the parent file. |
| `version_number` | INT | NOT NULL | Sequential version number for this file. |
| `minio_key` | VARCHAR(500) | NOT NULL | MinIO storage path for this specific version. |
| `size` | BIGINT | NOT NULL | File size in bytes for this version. |
| `created_at` | TIMESTAMPTZ | NOT NULL, DEFAULT now() | Upload timestamp for this version. |

## Indexes

- `idx_file_versions_file_version`: UNIQUE index on `(file_id, version_number)`.
- `idx_file_versions_file`: Index on `file_id`.

## Relationships

- `N:1` with `files`
