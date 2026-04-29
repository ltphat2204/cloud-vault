# Test Cases: Shares Module

## Overview
These test cases verify the Shares module, covering internal resource sharing, share management, and public link sharing (with optional password/expiry).

---

### TC-SHARE-01: Share Resource with User (Internal)
**Description:** Verify that an owner can share a resource (Project, Folder, File) with another registered user.
**Endpoints:** `POST /api/v1/shares`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit valid `resourceType`, `resourceId`, `userEmail`, and `permission` (VIEW). | 201 Created; Share record saved; Recipient can now access the resource; **Notification created for recipient**. |
| 2 | Submit with non-existent user email. | 404 Not Found; Error "User not found". |
| 3 | Submit sharing a resource not owned by the requester. | 403 Forbidden; Error "You do not have permission to share this resource". |
| 4 | Submit duplicate share (same user, same resource). | 400 Bad Request; Error "Resource already shared with this user". |

---

### TC-SHARE-02: Update Share Permission
**Description:** Verify that an owner can change the permission level of an existing share.
**Endpoints:** `PATCH /api/v1/shares/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Change `permission` from VIEW to EDIT for an existing share. | 200 OK; Permission updated in database; **Notification created for recipient**. |
| 2 | Attempt to update a share for a resource not owned by requester. | 403 Forbidden. |
| 3 | Attempt to update a non-existent share ID. | 404 Not Found. |

---

### TC-SHARE-03: Revoke Share (Delete)
**Description:** Verify that an owner can revoke access by deleting the share record.
**Endpoints:** `DELETE /api/v1/shares/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Delete an existing share record. | 200 OK; Share record removed; Recipient loses access; **Notification created for recipient**. |
| 2 | Attempt to delete a share for a resource not owned by requester. | 403 Forbidden. |

---

### TC-SHARE-04: Create Public Link
**Description:** Verify that an owner can generate a public access link.
**Endpoints:** `POST /api/v1/shares/public`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit `resourceType` and `resourceId`. | 201 Created; `access_token` generated; `shared_with_user_id` is NULL. |
| 2 | Submit with `password` and `expiresAt`. | 201 Created; `password_hash` stored; `expires_at` set. |
| 3 | Attempt for resource not owned by requester. | 403 Forbidden. |

---

### TC-SHARE-05: Access Public Link
**Description:** Verify that a public link allows access to a resource without authentication.
**Endpoints:** `GET /api/v1/shares/public/{token}`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Access valid token (no password). | 200 OK; Resource metadata/content returned. |
| 2 | Access token with password (correct password). | 200 OK. |
| 3 | Access token with password (wrong password). | 401 Unauthorized; Error "Invalid password". |
| 4 | Access expired token. | 410 Gone or 404 Not Found; Error "Link has expired". |
| 5 | Access revoked/deleted token. | 404 Not Found. |

---

### TC-SHARE-06: List Shares for Resource
**Description:** Verify that an owner can list all active shares for a specific resource.
**Endpoints:** `GET /api/v1/shares/resource/{type}/{id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Request shares for an owned resource. | 200 OK; Returns list of internal shares and public links. |
| 2 | Request for resource not owned. | 403 Forbidden. |

---

### TC-SHARE-07: List Resources Shared With Me
**Description:** Verify that a user can list all resources shared with them, sorted by shared date descending.
**Endpoints:** `GET /api/v1/shares/shared-with-me`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint as a user with multiple shared resources. | 200 OK; Returns list of shared resources; Sorted by `sharedAt` descending. |
| 2 | Verify that resources the user owns are NOT included. | List only contains resources shared by others. |
| 3 | Verify that revoked shares are NOT included. | Revoked shares are absent. |
| 4 | Call endpoint as a user with no shared resources. | 200 OK; Returns an empty list `[]`. |
| 5 | Verify each shared resource contains `projectId` and `folderId`. | Response includes parent project/folder context for navigation. |

---

### TC-SHARE-08: Access Shared Resource via Project Context
**Description:** Verify that a user who has access to a specific folder/file can access the project metadata and list resources within that project context.
**Endpoints:** `GET /api/v1/projects/{id}`, `GET /api/v1/folders?projectId={id}`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Share a **FOLDER** with User B from Project A. | 201 Created. |
| 2 | User B requests Project A details (`GET /projects/{projectAId}`). | 200 OK; User B can see project name/metadata (implied view access). |
| 3 | User B lists folders in Project A (`GET /folders?projectId={projectAId}`). | 200 OK; User B can list folders they have access to. |
| 4 | User B lists files in shared Folder. | 200 OK; User B can see files within the shared folder. |
| 5 | User B attempts to access a sibling folder in Project A that was NOT shared. | 403 Forbidden or 404 Not Found (depending on listing implementation). |
