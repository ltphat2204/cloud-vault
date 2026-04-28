# Trash API Specification

The Trash API provides endpoints for managing soft-deleted files and folders. Users can list, restore, or permanently delete items from their trash.

## Endpoints

### 1. List Trash Items
Returns a list of all soft-deleted projects, files and folders for the authenticated user.

- **URL**: `GET /api/v1/trash`
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      {
        "id": "uuid",
        "name": "filename.txt",
        "type": "FILE",
        "size": 1024,
        "deletedAt": "2024-04-28T10:00:00",
        "projectId": "uuid",
        "originalPath": "/Folder/Subfolder"
      },
      {
        "id": "project-uuid",
        "name": "Project Name",
        "type": "PROJECT",
        "size": 0,
        "deletedAt": "2024-04-28T10:00:00",
        "projectId": "project-uuid",
        "originalPath": null
      }
    ]
  }
  ```

---

### 2. Batch Restore Items
Restores specified items from trash back to their original locations.

- **URL**: `POST /api/v1/trash/restore`
- **Auth required**: Yes
- **Request Body (`TrashBatchRequest`)**:
  ```json
  {
    "itemIds": ["uuid1", "uuid2"]
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Items restored successfully"
  }
  ```

---

### 3. Batch Permanent Delete
Permanently deletes the specified items from storage and database.

- **URL**: `DELETE /api/v1/trash`
- **Auth required**: Yes
- **Request Body (`TrashBatchRequest`)**:
  ```json
  {
    "itemIds": ["uuid1", "uuid2"]
  }
  ```
- **Success Response**: `204 No Content`

---

### 4. Empty Trash
Permanently deletes all items in the authenticated user's trash.

- **URL**: `DELETE /api/v1/trash/empty`
- **Auth required**: Yes
- **Success Response**: `204 No Content`

---

### 5. Recover All
Restores all items currently in the user's trash.

- **URL**: `POST /api/v1/trash/recover-all`
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "All items restored successfully"
  }
  ```

## Schemas

### TrashItemDto
| Field | Type | Description |
| --- | --- | --- |
| `id` | UUID | Unique identifier |
| `name` | String | Item name |
| `type` | String | Type: `FILE`, `FOLDER`, or `PROJECT` |
| `size` | Long | Size in bytes (null for folders) |
| `deletedAt` | LocalDateTime | Timestamp of deletion |
| `projectId` | UUID | Associated project ID |
| `originalPath` | String | Human-readable path before deletion |

### TrashBatchRequest
| Field | Type | Description |
| --- | --- | --- |
| `itemIds` | List<UUID> | List of IDs to process |
