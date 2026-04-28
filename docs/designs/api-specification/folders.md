# Folders API Specification

The Folders API allows for the hierarchical organization of files within a project.

## Endpoints

### 1. Create Folder
Creates a new folder within a project or another folder.

- **URL**: `/api/v1/folders`
- **Method**: `POST`
- **Auth required**: Yes
- **Request Body**:
  ```json
  {
    "name": "Documents",
    "projectId": "uuid-of-project",
    "parentFolderId": "uuid-of-parent-folder" (optional)
  }
  ```
- **Success Response**: `201 Created`
  ```json
  {
    "success": true,
    "message": "Folder created successfully",
    "data": {
      "id": "uuid",
      "name": "Documents",
      "projectId": "uuid",
      "parentFolderId": "uuid",
      "createdAt": "timestamp"
    }
  }
  ```

### 2. Get Folder Details
Retrieves metadata for a specific folder.

- **URL**: `/api/v1/folders/{id}`
- **Method**: `GET`
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "id": "uuid",
      "name": "Documents",
      "projectId": "uuid",
      "parentFolderId": "uuid"
    }
  }
  ```

### 3. List Folders
Lists folders within a project or a specific parent folder.

- **URL**: `/api/v1/folders`
- **Method**: `GET`
- **Parameters**:
    - `projectId` (required): UUID of the project.
    - `parentFolderId` (optional): UUID of the parent folder to list contents from.
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": [
      {
        "id": "uuid",
        "name": "Folder 1"
      }
    ]
  }
  ```

### 4. Update Folder
Renames an existing folder.

- **URL**: `/api/v1/folders/{id}`
- **Method**: `PATCH`
- **Auth required**: Yes
- **Request Body**:
  ```json
  {
    "name": "New Name"
  }
  ```
- **Success Response**: `200 OK`

### 5. Move Folder
Moves a folder to a new parent location within the same project.

- **URL**: `/api/v1/folders/{id}/move`
- **Method**: `PATCH`
- **Auth required**: Yes
- **Request Body**:
  ```json
  {
    "targetParentFolderId": "uuid-of-new-parent" (optional for root)
  }
  ```
- **Success Response**: `200 OK`

### 6. Delete Folder
Soft deletes a folder and all its contents (subfolders and files).

- **URL**: `/api/v1/folders/{id}`
- **Method**: `DELETE`
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Folder deleted successfully"
  }
  ```
