# Search Module

The Search module provides global and scoped search capabilities across the CloudVault platform, allowing users to efficiently locate files and folders by name.

## Features

- **Global Search**: Search for files and folders across all projects owned by or shared with the authenticated user.
- **Scoped Search (Project)**: Restrict search results to a specific project to reduce clutter and find relevant items faster.
- **Scoped Search (Folder)**: Further refine search to a specific folder within a project.
- **Unified Results**: Provides a consistent response format for both files and folders, including type markers and parent context.
- **Security-Aware**: Automatically filters results based on user access permissions (ownership and shared status).
- **Case-Insensitive Matching**: Search queries match resource names regardless of casing.

## Module Structure

The module follows Clean Architecture to ensure business logic remains decoupled from external infrastructure:

- **presentation**: REST controllers for exposing search endpoints and handling query parameters.
- **application**: Orchestrates search logic by aggregating data from the File and Folder modules while enforcing security boundaries.
- **domain**: Defines data structures for search results (`SearchResponse`).
- **infrastructure**: (Not applicable) Uses existing repositories from the Files and Folders modules.

## Key Components

- `ISearchService`: Interface defining the core search logic and filtering rules.
- `SearchServiceImpl`: Implementation that coordinates with `IFileRepository`, `IFolderRepository`, and `ShareService`.
- `SearchResponse`: Unified data transfer object representing a matched file or folder.
- `SearchController`: Presentation layer handling the `GET /api/v1/search` endpoint.
