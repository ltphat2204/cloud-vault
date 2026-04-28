# Projects API Specification

Project management APIs allow users to organize their storage into separate projects.

## Base Path
`/api/v1/projects`

## Endpoints

### 1. Create Project
`POST /`

Creates a new project owned by the authenticated user.

**Request Body (`CreateProjectRequest`):**
- `name` (String): The name of the project.

**Response Data (`ProjectDto`):** Details of the newly created project.

---

### 2. List Projects
`GET /`

Retrieves all projects belonging to the currently authenticated user.

**Response Data (`List<ProjectDto>`):** A list of project objects.

---

### 3. Get Project Details
`GET /{id}`

Fetches details of a specific project.

**Path Variables:**
- `id` (UUID): Unique ID of the project.

**Response Data (`ProjectDto`):** Detailed project information.

---

### 4. Update Project
`PATCH /{id}`

Modifies an existing project's name.

**Path Variables:**
- `id` (UUID): Unique ID of the project.

**Request Body (`UpdateProjectRequest`):**
- `name` (String): The new name for the project.

**Response Data (`ProjectDto`):** Updated project information.

---

### 5. Delete Project
`DELETE /{id}`

Permanently removes a project and its associated metadata.

**Path Variables:**
- `id` (UUID): Unique ID of the project.

---

## Schemas

### ProjectDto
| Field | Type | Description |
| --- | --- | --- |
| `id` | UUID | Unique identifier |
| `name` | String | Project name |
| `ownerId` | UUID | ID of the user who owns the project |
| `createdAt` | LocalDateTime | Timestamp of creation |
| `updatedAt` | LocalDateTime | Timestamp of last update |
| `deletedAt` | LocalDateTime | Timestamp of deletion (if applicable) |

### CreateProjectRequest / UpdateProjectRequest
| Field | Type | Description |
| --- | --- | --- |
| `name` | String | Name of the project |
