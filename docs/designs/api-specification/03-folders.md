# Folders API Specification

The Folders API allows for the hierarchical organization of files within a project.

## Endpoints

### 1. Create Folder
Creates a new folder within a project or another folder.

- **URL**: `POST /api/v1/folders`
- **Auth required**: Yes
- **Request Body (`CreateFolderRequest`)**:
  ```json
  {
    "name": "Documents",
    "projectId": "project-uuid",
    "parentFolderId": "parent-folder-uuid" (optional)
  }
  ```
- **Success Response**: `201 Created`
  ```json
  {
    "success": true,
    "message": "Folder created successfully",
    "data": {
      "id": "folder-uuid",
      "name": "Documents",
      "projectId": "project-uuid",
      "parentFolderId": "parent-folder-uuid",
      "createdAt": "2024-04-28T10:00:00"
    }
  }
  ```

---

### 2. Get Folder Details
Retrieves metadata for a specific folder.

- **URL**: `GET /api/v1/folders/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the folder.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "id": "folder-uuid",
      "name": "Documents",
      "projectId": "project-uuid",
      "parentFolderId": "parent-folder-uuid",
      "createdAt": "2024-04-28T10:00:00",
      "updatedAt": "2024-04-28T10:05:00"
    }
  }
  ```

---

### 3. List Folders
Lists folders within a project or a specific parent folder.

- **URL**: `GET /api/v1/folders`
- **Auth required**: Yes
- **Query Parameters**:
  - `projectId` (required): UUID of the project.
  - `parentFolderId` (optional): UUID of the parent folder to list contents from.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      {
        "id": "folder-uuid-1",
        "name": "Subfolder A",
        "projectId": "project-uuid",
        "parentFolderId": "parent-folder-uuid"
      },
      {
        "id": "folder-uuid-2",
        "name": "Subfolder B",
        "projectId": "project-uuid",
        "parentFolderId": "parent-folder-uuid"
      }
    ]
  }
  ```

---

### 4. Get Folder Path
Retrieves the hierarchical path (breadcrumbs) for a specific folder.

- **URL**: `GET /api/v1/folders/{id}/path`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the folder.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      {
        "id": "root-folder-uuid",
        "name": "Project Name",
        "parentFolderId": null
      },
      {
        "id": "subfolder-uuid",
        "name": "Subfolder",
        "parentFolderId": "root-folder-uuid"
      }
    ]
  }
  ```

---

### 5. List All Folders
Lists all folders within a project regardless of hierarchy.

- **URL**: `GET /api/v1/folders/all`
- **Auth required**: Yes
- **Query Parameters**:
  - `projectId` (required): UUID of the project.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      { "id": "uuid1", "name": "Root", "parentFolderId": null },
      { "id": "uuid2", "name": "Docs", "parentFolderId": "uuid1" },
      { "id": "uuid3", "name": "Images", "parentFolderId": "uuid1" }
    ]
  }
  ```

---

### 6. Update Folder
Renames an existing folder.

- **URL**: `PATCH /api/v1/folders/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the folder.
- **Request Body (`UpdateFolderRequest`)**:
  ```json
  {
    "name": "New Folder Name"
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Folder updated successfully",
    "data": {
      "id": "folder-uuid",
      "name": "New Folder Name",
      "updatedAt": "2024-04-28T10:10:00"
    }
  }
  ```

---

### 7. Move Folder
Moves a folder to a new parent location within the same project.

- **URL**: `PATCH /api/v1/folders/{id}/move`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the folder.
- **Request Body (`MoveFolderRequest`)**:
  ```json
  {
    "targetParentFolderId": "new-parent-folder-uuid" (optional for root)
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Folder moved successfully",
    "data": {
      "id": "folder-uuid",
      "name": "Documents",
      "parentFolderId": "new-parent-folder-uuid"
    }
  }
  ```

---

### 8. Delete Folder
Soft deletes a folder and all its contents (subfolders and files). The deleted items can be managed via the Trash API.

- **URL**: `DELETE /api/v1/folders/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the folder.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Folder deleted successfully"
  }
  ```

## Schemas

### FolderDto
| Field | Type | Description |
| --- | --- | --- |
| `id` | UUID | Unique identifier |
| `name` | String | Folder name |
| `projectId` | UUID | Project ID this folder belongs to |
| `parentFolderId` | UUID | Parent folder ID (null for root) |
| `createdAt` | LocalDateTime | Creation timestamp |
| `updatedAt` | LocalDateTime | Last update timestamp |

## Real-Time Updates (WebSockets)

The Folders module emits WebSocket events to keep connected clients synchronized. These events are transient and are **not** persisted in the database history. They are sent to the dedicated synchronization queue: `/user/queue/sync`.

### Event Types (RealTimeUpdateType)

- `FOLDER_CREATED`: Emitted when a new folder is created.
- `FOLDER_UPDATED`: Emitted when a folder is renamed.
- `FOLDER_DELETED`: Emitted when a folder is soft-deleted.
- `FOLDER_MOVED`: Emitted when a folder is moved to a different location.

### Sync Event Payload (SyncEventDTO)

```json
{
  "type": "FOLDER_CREATED",
  "timestamp": "2024-04-28T10:00:00",
  "metadata": {
    "projectId": "project-uuid",
    "parentFolderId": "parent-folder-uuid",
    "resourceId": "folder-uuid",
    "resourceName": "Documents"
  }
}
```
