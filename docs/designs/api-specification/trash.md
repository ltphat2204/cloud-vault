# API Specification: Trash Management

## Base URL: `/api/v1/trash`

### 1. List Trash Items
- **Endpoint**: `GET /`
- **Description**: Returns a list of all soft-deleted files and folders for the authenticated user.
- **Response**:
    - `200 OK`: Array of `TrashItemDto`
    ```json
    [
      {
        "id": "uuid",
        "name": "filename.txt",
        "type": "FILE",
        "size": 1024,
        "deletedAt": "2024-04-28T10:00:00",
        "projectId": "uuid",
        "originalPath": "/Folder/Subfolder"
      }
    ]
    ```

### 2. Batch Restore Items
- **Endpoint**: `POST /restore`
- **Description**: Restores the specified items from trash.
- **Request Body**:
    ```json
    {
      "itemIds": ["uuid1", "uuid2"]
    }
    ```
- **Response**:
    - `200 OK`: Success message.

### 3. Batch Permanent Delete
- **Endpoint**: `DELETE /`
- **Description**: Permanently deletes the specified items.
- **Request Body**:
    ```json
    {
      "itemIds": ["uuid1", "uuid2"]
    }
    ```
- **Response**:
    - `204 No Content`: Success.

### 4. Empty Trash
- **Endpoint**: `DELETE /empty`
- **Description**: Permanently deletes all items in the user's trash.
- **Response**:
    - `204 No Content`: Success.

### 5. Recover All
- **Endpoint**: `POST /recover-all`
- **Description**: Restores all items in the user's trash.
- **Response**:
    - `200 OK`: Success message.
