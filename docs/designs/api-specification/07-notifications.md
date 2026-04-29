# Notification API Specification

Endpoints for managing user notifications.

## Base URL
`/api/v1/notifications`

## Endpoints

### 1. List Notifications
Fetch all notifications for the authenticated user, sorted by delivery time (newest first).

- **URL**: `/`
- **Method**: `GET`
- **Auth Required**: YES
- **Query Parameters**:
  - `page` (int, default: 0)
  - `size` (int, default: 20)
  - `unreadOnly` (boolean, default: false)

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:
    ```json
    {
      "success": true,
      "message": "Notifications retrieved successfully",
      "data": {
        "content": [
          {
            "id": "uuid",
            "type": "SHARE_RECEIVED",
            "message": "User X shared project Y with you",
            "isRead": false,
            "metadata": {
              "resourceId": "uuid",
              "senderName": "User X"
            },
            "createdAt": "2024-04-29T10:00:00Z"
          }
        ],
        "pageable": { ... },
        "totalElements": 1
      },
      "timestamp": "..."
    }
    ```

### 2. Mark as Read
Mark a specific notification as read.

- **URL**: `/{id}/read`
- **Method**: `PATCH`
- **Auth Required**: YES

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:
    ```json
    {
      "success": true,
      "message": "Notification marked as read",
      "data": null,
      "timestamp": "..."
    }
    ```

### 3. Mark All as Read
Mark all notifications for the current user as read.

- **URL**: `/read-all`
- **Method**: `PATCH`
- **Auth Required**: YES

- **Success Response**:
  - **Code**: 200 OK
  - **Content**:
    ```json
    {
      "success": true,
      "message": "All notifications marked as read",
      "data": null,
      "timestamp": "..."
    }
    ```
