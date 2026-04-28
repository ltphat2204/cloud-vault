# Trash Module

The Trash module provides a centralized management system for soft-deleted files and folders. It allows users to view, restore, or permanently remove items that have been deleted.

## Features

- **Centralized Management**: View all soft-deleted items (files and folders) in a single location across all projects.
- **Batch Operations**: Support for batch restoration and batch permanent deletion of multiple items.
- **Intelligent Restoration**: Automatically handles orphaned items by moving restored folders or files to the root if their original parent is still in trash or missing.
- **Recursive Operations**: Restoration and permanent deletion propagate recursively through folder hierarchies.
- **MinIO Cleanup**: Permanent deletion of files ensures that all binary content and versions are removed from the S3 object store.
- **Empty Trash**: One-click permanent removal of all items in the user's trash bin.

## Module Structure

The module follows Clean Architecture:

- **presentation**: REST controllers (`TrashController`) for trash management endpoints.
- **application**: Services (`ITrashService`), DTOs, and mappers for coordinating file and folder restoration/deletion.
- **domain**: Core models (`TrashItem`) and repository interfaces for querying deleted items.
- **infrastructure**: Persistence implementation using `TrashRepositoryAdapter` which aggregates data from files and folders.

## Key Components

- `ITrashService`: Primary interface for trash operations (list, restore, delete, empty).
- `TrashItem`: Domain model representing an item in trash (FILE or FOLDER).
- `TrashRepositoryAdapter`: Infrastructure adapter that combines deleted metadata from both file and folder repositories.
- `TrashBatchRequest`: DTO for batch restore and delete operations.

## Restoration Logic

When an item is restored:
1. The system checks if the parent folder (if any) still exists and is not in trash.
2. If the parent is missing or deleted, the restored item is moved to the root of the project to ensure accessibility.
3. For folders, the restoration is recursive, restoring subfolders and files that were deleted in the same context.
