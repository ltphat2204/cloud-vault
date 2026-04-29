# Test Cases: Folders Module

## Overview
These test cases verify the Folders module, focusing on hierarchical CRUD operations, movement validation (no circularity), and recursive soft deletion.

---

### TC-FOLD-01: Create Folder
**Description:** Verify that an authenticated user can create a folder in a project or subfolder.
**Endpoints:** `POST /api/v1/folders`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit valid `name`, `projectId`, and `parentFolderId=null`. | 201 Created; Folder created at project root. |
| 2 | Submit valid `name`, `projectId`, and valid `parentFolderId`. | 201 Created; Subfolder created. |
| 3 | Submit with name that already exists in the same parent. | 409 Conflict; Error "already exists". |
| 4 | Submit for a project owned by a different user. | 403 Forbidden. |

---

### TC-FOLD-02: List Folders
**Description:** Verify retrieval of folder listings within a project or parent folder.
**Endpoints:** `GET /api/v1/folders?projectId={id}&parentFolderId={id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Query folders with `parentFolderId=null`. | 200 OK; Returns list of root folders for the project. |
| 2 | Query folders with a specific `parentFolderId`. | 200 OK; Returns list of subfolders. |
| 3 | Verify soft-deleted folders are excluded. | Deleted folders are not present in the list. |

---

### TC-FOLD-03: Move Folder (Hierarchy Validation)
**Description:** Verify moving a folder and ensuring no circular references.
**Endpoints:** `PATCH /api/v1/folders/{id}/move`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Move folder to a different parent subfolder. | 200 OK; `parentFolderId` updated. |
| 2 | Attempt to move a folder into itself. | 400 Bad Request; Error "Cannot move folder into itself". |
| 3 | Attempt to move a folder into one of its own subfolders. | 400 Bad Request; Error "Cannot move folder into its own subfolder". |
| 4 | Move to a parent folder in a different project. | 400 Bad Request; Project mismatch validation. |

---

### TC-FOLD-04: Update Folder (Rename)
**Description:** Verify renaming a folder.
**Endpoints:** `PATCH /api/v1/folders/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit new name for a folder. | 200 OK; Name updated. |
| 2 | Submit name that conflicts with another folder in the same parent. | 409 Conflict. |

---

### TC-FOLD-05: Delete Folder (Recursive Soft Delete)
**Description:** Verify that deleting a folder also soft-deletes its contents.
**Endpoints:** `DELETE /api/v1/folders/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call delete for a folder with subfolders. | 200 OK; Folder and all descendants marked with `deleted_at`. |
| 2 | Attempt to get a subfolder of the deleted folder. | 404 Not Found. |
| 3 | Verify records still exist in DB with `deleted_at` timestamp. | Data persistence maintained for recovery. |

---

### TC-FOLD-06: Fetch Breadcrumbs
**Description:** Verify that the system can recursively fetch the folder hierarchy for navigation.
**Endpoints:** `GET /api/v1/folders/{id}/breadcrumbs`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint for a folder nested 3 levels deep. | 200 OK; Returns list of 3 parent folders in correct order (Root -> Sub1 -> Sub2). |
| 2 | Call endpoint for a root folder. | 200 OK; Returns single breadcrumb. |

---

### TC-FOLD-07: Project-wide Folder Listing
**Description:** Verify retrieval of all folders within a project regardless of hierarchy.
**Endpoints:** `GET /api/v1/folders/all?projectId={id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint for a project with multiple nested folders. | 200 OK; Returns flat list of all folders in the project. |
| 2 | Verify that soft-deleted folders are excluded. | Deleted folders not in the list. |

---

### TC-FOLD-08: Real-time Update Verification
**Description:** Verify that folder operations trigger WebSocket synchronization events for all users with access to the project.
**WebSocket:** `/user/queue/sync`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | User A creates a folder in a shared project. | User B (who has access) receives a `FOLDER_CREATED` event via WebSocket. |
| 2 | User A renames the folder. | User B receives a `FOLDER_UPDATED` event. |
| 3 | User A moves the folder. | User B receives a `FOLDER_MOVED` event. |
| 4 | User A deletes the folder. | User B receives a `FOLDER_DELETED` event. |
