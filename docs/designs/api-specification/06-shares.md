# Shares API Specification

The Shares API allows users to share projects, folders, and files with other users or via public links.

## Endpoints

### 1. Share Resource (Internal)
Shares a resource with another registered user via email.

- **URL**: `POST /api/v1/shares`
- **Auth required**: Yes
- **Request Body (`ShareResourceRequest`)**:
  ```json
  {
    "resourceType": "PROJECT",
    "resourceId": "uuid",
    "userEmail": "recipient@example.com",
    "permission": "VIEW"
  }
  ```
- **Success Response**: `201 Created`
  ```json
  {
    "success": true,
    "message": "Resource shared successfully",
    "data": {
      "id": "share-uuid",
      "resourceType": "PROJECT",
      "resourceId": "uuid",
      "sharedWithUser": {
        "id": "user-uuid",
        "email": "recipient@example.com"
      },
      "permission": "VIEW",
      "createdAt": "2024-04-29T10:00:00"
    }
  }
  ```

---

### 2. Update Share Permission
Updates the access level for an existing share.

- **URL**: `PATCH /api/v1/shares/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the share record.
- **Request Body (`UpdateShareRequest`)**:
  ```json
  {
    "permission": "EDIT"
  }
  ```
- **Success Response**: `200 OK`

---

### 3. Revoke Share
Removes access for a shared user or deletes a public link.

- **URL**: `DELETE /api/v1/shares/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `id` (UUID): Unique ID of the share record.
- **Success Response**: `200 OK`

---

### 4. Create Public Link
Generates a public access token for a resource.

- **URL**: `POST /api/v1/shares/public`
- **Auth required**: Yes
- **Request Body (`CreatePublicLinkRequest`)**:
  ```json
  {
    "resourceType": "FILE",
    "resourceId": "uuid",
    "password": "optional-password",
    "expiresAt": "2024-05-29T10:00:00"
  }
  ```
- **Success Response**: `201 Created`
  ```json
  {
    "success": true,
    "data": {
      "id": "share-uuid",
      "accessToken": "unique-token-uuid",
      "publicUrl": "https://cloudvault.com/s/unique-token-uuid",
      "expiresAt": "2024-05-29T10:00:00"
    }
  }
  ```

---

### 5. Get Shares for Resource
Lists all active shares (internal and public) for a specific resource.

- **URL**: `GET /api/v1/shares/resource/{type}/{id}`
- **Auth required**: Yes
- **Path Variables**:
  - `type` (String): `PROJECT`, `FOLDER`, or `FILE`.
  - `id` (UUID): Resource ID.
- **Success Response**: `200 OK`

---

### 6. List Resources Shared With Me
Retrieves all resources that have been shared with the authenticated user.

- **URL**: `GET /api/v1/shares/shared-with-me`
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      {
        "shareId": "share-uuid",
        "resourceType": "FOLDER",
        "resourceId": "uuid",
        "resourceName": "Shared Folder",
        "sharedBy": "owner@example.com",
        "permission": "VIEW",
        "sharedAt": "2024-04-29T11:00:00"
      }
    ]
  }
  ```

---

### 7. Access Public Link
Retrieves resource metadata using a public token.

- **URL**: `GET /api/v1/shares/public/{token}`
- **Auth required**: No (Optional password via query or header)
- **Path Variables**:
  - `token` (UUID): Public access token.
- **Success Response**: `200 OK`

## Schemas

### ShareResponse
| Field | Type | Description |
| --- | --- | --- |
| `id` | UUID | Unique identifier |
| `resourceType` | String | PROJECT, FOLDER, FILE |
| `resourceId` | UUID | Target resource ID |
| `sharedWithUser` | Object | User details (null for public links) |
| `permission` | String | VIEW, EDIT |
| `accessToken` | UUID | Public token (null for internal shares) |
| `expiresAt` | LocalDateTime | Optional expiration |
| `createdAt` | LocalDateTime | Timestamp |
