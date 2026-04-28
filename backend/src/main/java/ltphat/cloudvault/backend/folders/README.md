# Folders Module

The Folders module provides hierarchical organization units within projects. It allows users to create, rename, move, and delete folders for better file management.

## Features

- **Hierarchical Organization**: Folders can be nested within other folders or directly under a project.
- **Move Folders**: Move folders within a project while maintaining integrity (preventing circular references).
- **Soft Deletion**: Folders and their subfolders are soft-deleted via the `deleted_at` timestamp.
- **Name Uniqueness**: Ensures folder names are unique within the same parent location.
- **Project Context**: Each folder is strictly associated with a project.
- **Breadcrumb Paths**: Recursively fetch folder hierarchy for smooth UI navigation and deep-linking support.
- **Project-wide Listing**: Support for listing all folders within a project regardless of hierarchy.

## Module Structure

The module follows Clean Architecture:

- **presentation**: REST controllers for external interaction.
- **application**: Services, DTOs, and mappers for use case implementation.
- **domain**: Core models, repository interfaces, and custom exceptions.
- **infrastructure**: Persistence implementation using JPA and Spring Data.

## Key Components

- `IFolderService`: Primary interface for folder operations.
- `Folder`: Domain model representing a folder.
- `MoveFolderRequest`: Input DTO for folder movement operations.
- `FolderRepositoryAdapter`: Infrastructure adapter for persistence.

## Hierarchy Validation

When moving a folder, the system validates that:
1. The target parent folder exists.
2. The target parent is not the folder itself.
3. The target parent is not a descendant of the folder (preventing circularity).
4. The name is unique in the new location.
