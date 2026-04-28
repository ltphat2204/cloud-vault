# Projects API Specification

Project management APIs allow users to organize their storage into separate projects. Each project acts as a top-level container for folders and files.

## Endpoints

### 1. Create Project
Creates a new project owned by the authenticated user.

- **URL**: `POST /api/v1/projects`
- **Auth required**: Yes
- **Request Body (`CreateProjectRequest`)**:
  ```json
  {
    "name": "Personal Documents"
  }
  ```
- **Success Response**: `201 Created`
  ```json
  {
    "success": true,
    "message": "Project created successfully",
    "data": {
      "id": "project-uuid",
      "name": "Personal Documents",
      "ownerId": "user-uuid",
      "createdAt": "2024-04-28T10:00:00"
    }
  }
  ```

---

### 2. List Projects
Retrieves all projects belonging to the currently authenticated user.

- **URL**: `GET /api/v1/projects`
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      {
        "id": "project-uuid-1",
        "name": "Project Alpha",
        "ownerId": "user-uuid",
        "createdAt": "2024-04-28T09:00:00"
      },
      {
        "id": "project-uuid-2",
        "name": "Project Beta",
        "ownerId": "user-uuid",
        "createdAt": "2024-04-28T09:30:00"
      }
    ]
  }
  ```

---

### 3. Get Project Details
Fetches metadata for a specific project.

- **URL**: `GET /api/v1/projects/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the project.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "id": "project-uuid",
      "name": "Personal Documents",
      "ownerId": "user-uuid",
      "createdAt": "2024-04-28T10:00:00",
      "updatedAt": "2024-04-28T10:05:00"
    }
  }
  ```

---

### 4. Update Project
Modifies an existing project's metadata.

- **URL**: `PATCH /api/v1/projects/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the project.
- **Request Body (`UpdateProjectRequest`)**:
  ```json
  {
    "name": "New Project Name"
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Project updated successfully",
    "data": {
      "id": "project-uuid",
      "name": "New Project Name",
      "ownerId": "user-uuid",
      "updatedAt": "2024-04-28T10:10:00"
    }
  }
  ```

---

### 5. Soft Delete Project
Moves a project and its associated metadata to the trash. The project can be restored or permanently deleted via the Trash API.

- **URL**: `DELETE /api/v1/projects/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the project.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Project moved to trash successfully"
  }
  ```

## Schemas

### ProjectDto
| Field | Type | Description |
| --- | --- | --- |
| `id` | UUID | Unique identifier |
| `name` | String | Project name |
| `ownerId` | UUID | ID of the user who owns the project |
| `createdAt` | LocalDateTime | Timestamp of creation |
| `updatedAt` | LocalDateTime | Timestamp of last update |

### CreateProjectRequest / UpdateProjectRequest
| Field | Type | Description |
| --- | --- | --- |
| `name` | String | Name of the project |
