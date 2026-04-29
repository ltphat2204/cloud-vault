# Files Module

The Files Module is responsible for managing file metadata, versioning, and binary storage integration in CloudVault. It ensures that files are securely stored, versioned, and accessible to authorized users.

## Features

- **File Metadata Management**: Store and retrieve file information such as name, size, MIME type, and location.
- **Versioning System**: Automatically track file history. Every upload of an existing file creates a new version, preserving previous states.
- **Storage Integration**: Seamlessly integrates with MinIO (S3-compatible) for robust binary content storage.
- **Hierarchical Organization**: Supports organizing files within projects and folders.
- **Collaborative Access**: Fully integrates with the Sharing module to allow access for project members and public link holders.
- **Soft Deletion**: Implements soft deletion logic to allow recovery via the Trash module.

## Module Structure

Following Clean Architecture principles:

- **presentation**: REST controllers for file operations (upload, download, metadata updates).
- **application**: Business logic for file management, versioning, and coordination between domain and infrastructure.
- **domain**: Core entities (`File`, `FileVersion`) and repository interfaces.
- **infrastructure**: Concrete implementations for database persistence and external storage (MinIO).

## Key Components

- `IFileService`: Core application service for file and version management.
- `IFileRepository`: Domain interface for file metadata persistence.
- `IFileVersionRepository`: Domain interface for version history persistence.
- `MinioStorageAdapter`: Infrastructure adapter for S3-compatible storage operations.
- `FileDto` / `FileVersionDto`: Data transfer objects for API communication.
