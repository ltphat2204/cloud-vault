# Test Cases: Search Module

## Overview
These test cases verify the Search module, covering global search across all accessible projects and scoped search within specific projects or folders.

---

### TC-SEARCH-01: Global Search
**Description:** Verify that a user can search for files and folders across all projects they own or have access to.
**Endpoints:** `GET /api/v1/search`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit search query `q` that matches multiple files/folders across different projects. | 200 OK; Returns list of matched items; Each item includes type (FILE/FOLDER) and parent context. |
| 2 | Verify that results ONLY include items from owned or shared projects. | No items from unauthorized projects are returned. |
| 3 | Submit query with no matches. | 200 OK; Returns empty list `[]`. |
| 4 | Submit query with mixed casing (e.g., "REport"). | 200 OK; Returns matches regardless of case. |

---

### TC-SEARCH-02: Scoped Search (Project)
**Description:** Verify that a user can restrict search results to a specific project.
**Endpoints:** `GET /api/v1/search?q={query}&projectId={projectId}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit search query with valid `projectId` owned by the user. | 200 OK; Returns matches ONLY within the specified project. |
| 2 | Submit search query with `projectId` shared with the user. | 200 OK; Returns matches within the shared project. |
| 3 | Submit search query with `projectId` NOT accessible to the user. | 403 Forbidden; Error "Access denied to project". |
| 4 | Submit search query with non-existent `projectId`. | 404 Not Found or 403 Forbidden (depending on security implementation). |

---

### TC-SEARCH-03: Scoped Search (Folder)
**Description:** Verify that a user can restrict search results to a specific folder within a project.
**Endpoints:** `GET /api/v1/search?q={query}&projectId={projectId}&folderId={folderId}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit search query with valid `projectId` and `folderId` within that project. | 200 OK; Returns matches ONLY within the specified folder and its subfolders. |
| 2 | Submit search query with `folderId` that belongs to a different project than `projectId`. | 400 Bad Request; Error "Folder does not belong to specified project". |

---

### TC-SEARCH-04: Search Result Consistency
**Description:** Verify that search results provide necessary metadata for navigation.
**Endpoints:** `GET /api/v1/search`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Inspect search results for files. | Includes `id`, `name`, `type` (FILE), `projectId`, `folderId`, `mimeType`, `size`, and `updatedAt`. |
| 2 | Inspect search results for folders. | Includes `id`, `name`, `type` (FOLDER), `projectId`, and `updatedAt`. |
