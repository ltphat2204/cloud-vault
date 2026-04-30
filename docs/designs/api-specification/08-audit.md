# Audit API Specification

Endpoints for retrieving user activity logs and resource history.

## Base URL
`/api/v1/audit`

## Endpoints

### 1. List My Activities
Fetch all activity logs for the authenticated user using cursor-based pagination.

- **URL**: `/`
- **Method**: `GET`
- **Auth Required**: YES
- **Query Parameters**:
  - `cursor` (string, optional) - Opaque string for pagination.
  - `size` (int, default: 20) - Number of items to return.
  - `action` (string, optional) - Filter by action (e.g., FILE_UPLOADED)
  - `resourceType` (string, optional) - Filter by resource type (PROJECT, FOLDER, FILE)

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:
    ```json
    {
      "success": true,
      "message": "Activity logs retrieved successfully",
      "data": {
        "content": [
          {
            "id": "uuid",
            "userId": "uuid",
            "action": "FILE_UPLOADED",
            "resourceType": "FILE",
            "resourceId": "uuid",
            "details": {
              "fileName": "document.pdf",
              "size": 1024
            },
            "createdAt": "2024-04-29T10:00:00Z"
          }
        ],
        "nextCursor": "base64-string",
        "hasNext": true
      },
      "timestamp": "..."
    }
    ```

### 2. Get Resource History
Fetch the activity history for a specific resource using cursor-based pagination. The user must have permission (owner or shared access) to view the resource history.

- **URL**: `/resources/{resourceId}`
- **Method**: `GET`
- **Auth Required**: YES
- **Query Parameters**:
  - `cursor` (string, optional) - Opaque string for pagination.
  - `size` (int, default: 20) - Number of items to return.
  - `resourceType` (string, REQUIRED) - One of PROJECT, FOLDER, FILE

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:
    ```json
    {
      "success": true,
      "message": "Resource history retrieved successfully",
      "data": {
        "content": [
          {
            "id": "uuid",
            "userId": "uuid",
            "action": "FILE_RENAMED",
            "resourceType": "FILE",
            "resourceId": "{resourceId}",
            "details": {
              "oldName": "old.txt",
              "newName": "new.txt"
            },
            "createdAt": "2024-04-29T11:00:00Z"
          }
        ],
        "nextCursor": "base64-string",
        "hasNext": true
      },
      "timestamp": "..."
    }
    ```

- **Error Response**:
  - **Code**: 403 Forbidden (If user has no access to the resource)
  - **Code**: 404 Not Found (If resource does not exist)
