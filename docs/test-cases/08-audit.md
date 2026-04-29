# Test Cases: Audit Module

## Overview
These test cases verify the Audit module, covering activity logging, history retrieval, and security constraints.

---

### TC-AUDIT-01: List My Activities
**Description:** Verify that an authenticated user can retrieve their activity logs.
**Endpoints:** `GET /api/v1/audit`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint as an authenticated user with activities. | 200 OK; Returns list of activity logs; Sorted by `createdAt` descending. |
| 2 | Verify log structure includes `id`, `userId`, `action`, `resourceType`, `resourceId`, `details`, and `createdAt`. | All fields present and correctly typed. |
| 3 | Verify `details` contains relevant metadata (e.g., `fileName`). | Metadata correctly populated. |

---

### TC-AUDIT-02: Get Resource History
**Description:** Verify that an owner can retrieve the history of a specific resource.
**Endpoints:** `GET /api/v1/audit/resources/{id}?resourceType=FILE`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint for a resource owned by the user. | 200 OK; Returns only actions related to that specific resource ID. |
| 2 | Verify that actions from multiple users (if shared) are present. | Full history visible to the owner. |

---

### TC-AUDIT-03: Get Resource History (Shared User)
**Description:** Verify that a user with shared access can retrieve resource history.
**Endpoints:** `GET /api/v1/audit/resources/{id}?resourceType=FILE`
**Headers:** `Authorization: Bearer <shared_user_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint for a file shared with the user (VIEW or EDIT). | 200 OK; Returns resource history. |

---

### TC-AUDIT-04: Security - Access Control
**Description:** Verify that a user cannot see history of resources they don't own or have access to.
**Endpoints:** `GET /api/v1/audit/resources/{other_user_resource_id}`
**Headers:** `Authorization: Bearer <requester_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Attempt to retrieve history for a resource not owned by or shared with the user. | 403 Forbidden. |

---

### TC-AUDIT-05: Integration - File Upload
**Description:** Verify that uploading a file triggers an audit log.

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Upload a new file via `FileService`. | File uploaded successfully. |
| 2 | Check audit logs for the user. | A `FILE_UPLOADED` log entry exists for the new file. |

---

### TC-AUDIT-06: Integration - File Download
**Description:** Verify that downloading a file triggers an audit log.

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Download a file via `FileService`. | File downloaded successfully. |
| 2 | Check audit logs for the user. | A `FILE_DOWNLOADED` log entry exists. |

---

### TC-AUDIT-07: Pagination and Filtering
**Description:** Verify pagination and action filtering.
**Endpoints:** `GET /api/v1/audit?action=FILE_UPLOADED&page=0&size=10`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint with `action=FILE_UPLOADED`. | Returns only upload actions. |
| 2 | Verify pagination metadata. | `totalPages` and `totalElements` are correct. |
