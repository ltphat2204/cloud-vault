# Files API Specification

The Files API manages file metadata, versions, and provides endpoints for uploading and downloading content.

## Endpoints

### 1. Get File Details
Retrieves metadata for a specific file and its current active version.

- **URL**: `GET /api/v1/files/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the file.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "id": "file-uuid",
      "name": "report.pdf",
      "size": 102456,
      "mimeType": "application/pdf",
      "projectId": "project-uuid",
      "folderId": "folder-uuid",
      "versionNumber": 1,
      "createdAt": "2024-04-28T10:00:00",
      "updatedAt": "2024-04-28T10:00:00"
    }
  }
  ```

---

### 2. List Files
Lists files within a project or a specific parent folder.

- **URL**: `GET /api/v1/files`
- **Auth required**: Yes
- **Query Parameters**:
  - `projectId` (required): UUID of the project.
  - `folderId` (optional): UUID of the parent folder.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      {
        "id": "file-uuid-1",
        "name": "invoice.pdf",
        "size": 54321,
        "mimeType": "application/pdf"
      }
    ]
  }
  ```

---

### 3. Upload File
Uploads a new file or a new version of an existing file.

- **URL**: `POST /api/v1/files/upload`
- **Method**: `POST`
- **Auth required**: Yes
- **Content-Type**: `multipart/form-data`
- **Form Data Parameters**:
  - `projectId` (required): UUID of the project.
  - `folderId` (optional): UUID of the parent folder.
  - `file` (required): Binary file content.
- **Success Response**: `201 Created`
  ```json
  {
    "success": true,
    "message": "File uploaded successfully",
    "data": {
      "id": "file-uuid",
      "name": "report.pdf",
      "size": 102456,
      "versionNumber": 1
    }
  }
  ```

---

### 4. Download File
Downloads the current active version of a specific file.

- **URL**: `GET /api/v1/files/{id}/download`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the file.
- **Success Response**: `200 OK`
  - **Content-Type**: Matches file's mime type.
  - **Body**: Binary stream.

---

### 5. Get Version History
Retrieves all versions of a specific file.

- **URL**: `GET /api/v1/files/{id}/versions`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the file.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      {
        "id": "version-uuid-2",
        "versionNumber": 2,
        "size": 110000,
        "createdAt": "2024-04-28T11:00:00"
      },
      {
        "id": "version-uuid-1",
        "versionNumber": 1,
        "size": 102456,
        "createdAt": "2024-04-28T10:00:00"
      }
    ]
  }
  ```

---

### 6. Download Specific Version
Downloads a specific version of a file.

- **URL**: `GET /api/v1/files/{id}/versions/{versionNumber}/download`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the file.
  - `versionNumber` (Integer): The version number to download.
- **Success Response**: `200 OK`
  - **Body**: Binary stream.

---

### 7. Rename/Update Metadata
Updates file metadata (e.g., name).

- **URL**: `PATCH /api/v1/files/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the file.
- **Request Body (`UpdateFileRequest`)**:
  ```json
  {
    "name": "New Report Name.pdf"
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "File metadata updated successfully",
    "data": {
      "id": "file-uuid",
      "name": "New Report Name.pdf"
    }
  }
  ```

---

### 8. Move File
Moves a file to a different folder within the same project.

- **URL**: `PUT /api/v1/files/{id}/move`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the file.
- **Request Body (`MoveFileRequest`)**:
  ```json
  {
    "targetFolderId": "new-folder-uuid" (optional for root)
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "File moved successfully"
  }
  ```

---

### 9. Delete File
Soft deletes a file.

- **URL**: `DELETE /api/v1/files/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the file.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "File deleted successfully"
  }
  ```

## Schemas

### FileDto
| Field | Type | Description |
| --- | --- | --- |
| `id` | UUID | Unique identifier |
| `name` | String | File name |
| `size` | Long | File size in bytes |
| `mimeType` | String | MIME type |
| `projectId` | UUID | Project ID |
| `folderId` | UUID | Folder ID |
| `versionNumber` | Integer | Current active version |
| `createdAt` | LocalDateTime | Creation timestamp |
| `updatedAt` | LocalDateTime | Last update timestamp |
| `versionNumber` | Integer | Current active version |

## Real-Time Updates (WebSockets)

The Files module emits WebSocket events to keep connected clients synchronized. These events are transient and are **not** persisted in the database history. They are sent to the dedicated synchronization queue: `/user/queue/sync`.

### Event Types (RealTimeUpdateType)

- `FILE_CREATED`: Emitted when a new file is uploaded.
- `FILE_UPDATED`: Emitted when a file is renamed or a new version is uploaded.
- `FILE_DELETED`: Emitted when a file is soft-deleted.
- `FILE_MOVED`: Emitted when a file is moved to a different folder.

### Sync Event Payload (SyncEventDTO)

```json
{
  "type": "FILE_CREATED",
  "timestamp": "2024-04-28T10:00:00",
  "metadata": {
    "projectId": "project-uuid",
    "folderId": "folder-uuid",
    "resourceId": "file-uuid",
    "resourceName": "report.pdf"
  }
}
```
