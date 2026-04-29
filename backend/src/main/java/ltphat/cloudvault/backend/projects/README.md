# Projects Module

The Projects module manages user-created projects within CloudVault. It provides a standard set of CRUD operations for organizing resources and user data.

## Features

- **Create Projects**: Users can create new projects with names and descriptions.
- **Retrieve Projects**: Fetch details of a specific project by its unique ID.
- **List Projects**: Retrieve all projects owned by a specific user.
- **Update Projects**: Modify existing project information (e.g., name, description).
- **Collaborative Support**: Integrates with the Sharing module to allow secure access for collaborators via project-wide permission checks.
- **Soft Deletion**: Projects are moved to trash before permanent removal.
- **Automatic Root Initialization**: New projects automatically create a root folder named after the project to store all contents.

## Module Structure

The module follows Clean Architecture to ensure business logic remains decoupled from external infrastructure:

- **presentation**: Handles incoming HTTP requests and responses via REST controllers.
- **application**: Implements project management use cases through services and DTOs.
- **domain**: Defines the `Project` entity and the `IProjectRepository` interface.
- **infrastructure**: Provides concrete implementations for persistence (e.g., JpaProjectRepository).

## Key Components

- `IProjectService`: Interface defining the core operations for project management.
- `ProjectDto`: Data transfer object for project information.
- `CreateProjectRequest`/`UpdateProjectRequest`: Input DTOs for project operations.
