# Test Cases: Projects Module

## Overview
These test cases verify the Projects module, focusing on CRUD operations, ownership validation, and soft deletion.

---

### TC-PROJ-01: Create Project
**Description:** Verify that an authenticated user can create a new project.
**Endpoints:** `POST /api/v1/projects`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit valid `name` in request body. | 201 Created; Project record saved with correct `owner_id`; Root folder initialization TODO triggered. |
| 2 | Submit with empty or null `name`. | 400 Bad Request; Validation error message. |
| 3 | Submit without authentication. | 401 Unauthorized. |

---

### TC-PROJ-02: List Projects
**Description:** Verify that users can only see projects they own or have access to.
**Endpoints:** `GET /api/v1/projects`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint as a user with multiple projects. | 200 OK; Returns list of projects owned by the user. |
| 2 | Call endpoint as a user with no projects. | 200 OK; Returns an empty list `[]`. |
| 3 | Verify that soft-deleted projects are excluded. | Deleted projects are not present in the returned list. |

---

### TC-PROJ-03: Get Project by ID
**Description:** Verify project retrieval with ownership enforcement.
**Endpoints:** `GET /api/v1/projects/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint with valid ID owned by the user. | 200 OK; Returns project metadata. |
| 2 | Call endpoint with valid ID owned by a different user. | 403 Forbidden; Error "You do not have access to this project". |
| 3 | Call endpoint with non-existent UUID. | 404 Not Found; Error "Project not found". |
| 4 | Call endpoint with ID of a soft-deleted project. | 404 Not Found; Error "Project not found". |

---

### TC-PROJ-04: Update Project (Rename)
**Description:** Verify project renaming with ownership enforcement.
**Endpoints:** `PATCH /api/v1/projects/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit new `name` for a project owned by the user. | 200 OK; Project name updated; `updated_at` timestamp refreshed. |
| 2 | Submit update for a project owned by a different user. | 403 Forbidden. |
| 3 | Submit update for a soft-deleted project. | 404 Not Found. |
| 4 | Submit update with invalid name data. | 400 Bad Request. |

---

### TC-PROJ-05: Delete Project (Soft Delete)
**Description:** Verify project deletion (moving to trash).
**Endpoints:** `DELETE /api/v1/projects/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call delete for a project owned by the user. | 200 OK; `deleted_at` field set to current timestamp. |
| 2 | Call delete for a project owned by a different user. | 403 Forbidden. |
| 3 | Call delete for an already deleted project. | 404 Not Found. |
| 4 | Verify that the project is still in the database but hidden from normal queries. | Record exists with `deleted_at` != null. |

---

### TC-PROJ-06: Ownership & Security Context
**Description:** Verify that the system correctly identifies the project owner from the JWT.

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Create project using Token A (User A). | Project `owner_id` matches User A's UUID. |
| 2 | Attempt to view Project A using Token B (User B). | 403 Forbidden. |
