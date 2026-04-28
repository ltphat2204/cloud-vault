# Test Cases: Files Module

## Overview
These test cases verify the Files module, focusing on metadata CRUD, version management, movement between folders, and soft deletion.

---

### TC-FILE-01: View File Details
**Description:** Verify that an authenticated user can retrieve metadata for a specific file.
**Endpoints:** `GET /api/v1/files/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint with a valid file ID owned by the user. | 200 OK; Returns file metadata (name, size, type, version). |
| 2 | Call endpoint with a file ID owned by a different user. | 403 Forbidden. |
| 3 | Call endpoint with a non-existent file ID. | 404 Not Found. |
| 4 | Call endpoint with a soft-deleted file ID. | 404 Not Found. |

---

### TC-FILE-02: List Files
**Description:** Verify retrieval of file listings within a project or folder.
**Endpoints:** `GET /api/v1/files?projectId={id}&folderId={id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Query files with `folderId=null` (Project root). | 200 OK; Returns list of files at project root. |
| 2 | Query files with a specific `folderId`. | 200 OK; Returns list of files in that folder. |
| 3 | Verify soft-deleted files are excluded. | Deleted files are not present in the list. |

---

### TC-FILE-03: Update File Metadata (Rename)
**Description:** Verify renaming a file.
**Endpoints:** `PATCH /api/v1/files/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit new name for a file. | 200 OK; `name` updated; `updated_at` refreshed. |
| 2 | Submit name that conflicts with another file in the same folder. | 409 Conflict; Error "already exists". |
| 3 | Submit update for a file owned by a different user. | 403 Forbidden. |

---

### TC-FILE-04: Move File
**Description:** Verify moving a file to a different folder.
**Endpoints:** `PUT /api/v1/files/{id}/move`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Move file to a valid target folder in the same project. | 200 OK; `folderId` updated. |
| 2 | Move file to a folder in a different project. | 400 Bad Request; Project mismatch validation. |
| 3 | Move file to a location where a file with the same name already exists. | 409 Conflict. |

---

### TC-FILE-05: Delete File (Soft Delete)
**Description:** Verify soft deletion of a file.
**Endpoints:** `DELETE /api/v1/files/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call delete for a file owned by the user. | 200 OK; `deleted_at` field set. |
| 2 | Verify the file is no longer returned in listings or by ID. | 404 Not Found for subsequent requests. |
| 3 | Verify record persists in database with deletion timestamp. | Data maintained for Trash Bin functionality. |

---

### TC-FILE-06: File Versioning (Conceptual)
**Description:** Verify that new uploads create new versions and metadata reflects the latest state.
*(Note: Full upload logic to be implemented)*

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Upload a file with the same name to the same location. | Metadata updated with incremented `version_number`. |
| 2 | Retrieve file versions list. | Returns history of all uploaded versions. |
