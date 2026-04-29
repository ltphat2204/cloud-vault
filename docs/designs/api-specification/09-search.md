# Search API Specification

The Search API allows users to locate resources (files and folders) across their accessible projects using keyword-based matching and hierarchical scoping.

## Endpoints

### 1. Unified Search
Search for files and folders by name. Results can be global or restricted by project and folder.

- **URL**: `GET /api/v1/search`
- **Auth required**: Yes
- **Query Parameters**:
  - `q` (String, Required): Search keyword. Matches resource names (case-insensitive).
  - `projectId` (UUID, Optional): Restrict search to a specific project.
  - `folderId` (UUID, Optional): Restrict search to a specific folder (requires `projectId`).
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      {
        "id": "file-uuid",
        "name": "Project_Plan.pdf",
        "type": "FILE",
        "projectId": "project-uuid",
        "folderId": "folder-uuid",
        "mimeType": "application/pdf",
        "size": 102456,
        "updatedAt": "2024-04-29T10:00:00"
      },
      {
        "id": "folder-uuid",
        "name": "Design Assets",
        "type": "FOLDER",
        "projectId": "project-uuid",
        "updatedAt": "2024-04-29T09:30:00"
      }
    ]
  }
  ```

---

## Schemas

### SearchResponse
| Field | Type | Description |
| --- | --- | --- |
| `id` | UUID | Unique identifier of the resource |
| `name` | String | Name of the file or folder |
| `type` | String | Type of resource (`FILE` or `FOLDER`) |
| `projectId` | UUID | ID of the project containing the resource |
| `folderId` | UUID | ID of the parent folder (null for folders or files in project root) |
| `mimeType` | String | MIME type (files only) |
| `size` | Long | File size in bytes (files only) |
| `updatedAt` | LocalDateTime | Last modification timestamp |

---

## Authorization & Filtering

1.  **Ownership**: Users automatically have search access to all projects they own.
2.  **Shared Access**: Users have search access to projects shared with them (via the Shares module).
3.  **Scoped Validation**: When `projectId` is provided, the system verifies that the user has explicit or inherited access to that project before performing the search.
4.  **Deleted Items**: Items currently in the trash (soft-deleted) are excluded from search results.
