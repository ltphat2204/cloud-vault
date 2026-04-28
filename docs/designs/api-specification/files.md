# Files API Specification

The Files API manages file metadata, versions, and provides endpoints for uploading and downloading content.

## Endpoints

### 1. Get File Details
Retrieves metadata for a specific file and its current active version.

- **URL**: `/api/v1/files/{id}`
- **Method**: `GET`
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "id": "uuid",
      "name": "report.pdf",
      "size": 102456,
      "mimeType": "application/pdf",
      "projectId": "uuid",
      "folderId": "uuid",
      "versionNumber": 1,
      "createdAt": "timestamp",
      "updatedAt": "timestamp"
    }
  }
  ```

### 2. List Files
Lists files within a project or a specific parent folder.

- **URL**: `/api/v1/files`
- **Method**: `GET`
- **Parameters**:
    - `projectId` (required): UUID of the project.
    - `folderId` (optional): UUID of the parent folder.
- **Auth required**: Yes
- **Success Response**: `200 OK`

### 3. Rename/Update Metadata
Updates file metadata (e.g., name).

- **URL**: `/api/v1/files/{id}`
- **Method**: `PATCH`
- **Auth required**: Yes
- **Request Body**:
  ```json
  {
    "name": "New Name.pdf"
  }
  ```
- **Success Response**: `200 OK`

### 4. Move File
Moves a file to a different folder within the same project.

- **URL**: `/api/v1/files/{id}/move`
- **Method**: `PUT`
- **Auth required**: Yes
- **Request Body**:
  ```json
  {
    "targetFolderId": "uuid-of-new-folder" (optional for root)
  }
  ```
- **Success Response**: `200 OK`

### 5. Delete File
Soft deletes a file.

- **URL**: `/api/v1/files/{id}`
- **Method**: `DELETE`
- **Auth required**: Yes
- **Success Response**: `200 OK`

### 6. Upload File (Draft)
Uploads a new file or a new version of an existing file.

- **URL**: `/api/v1/files/upload`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Parameters**:
    - `projectId` (required)
    - `folderId` (optional)
- **Auth required**: Yes

### 7. Download File (Draft)
Downloads a specific file or version.

- **URL**: `/api/v1/files/{id}/download`
- **Method**: `GET`
- **Parameters**:
    - `version` (optional): Version number to download.
- **Auth required**: Yes
- **Success Response**: Binary stream.
