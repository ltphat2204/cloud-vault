# Test Cases: Notifications Module

## Overview
These test cases verify the Notifications module, covering notification retrieval, status updates, and security constraints.

---

### TC-NOTIF-01: List Notifications
**Description:** Verify that an authenticated user can retrieve their notifications.
**Endpoints:** `GET /api/v1/notifications`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint as an authenticated user with notifications. | 200 OK; Returns list of notifications; Sorted by `createdAt` descending. |
| 2 | Verify notification structure includes `id`, `type`, `message`, `isRead`, `metadata`, and `createdAt`. | All fields present and correctly typed. |
| 3 | Call endpoint as a user with no notifications. | 200 OK; Returns empty list `[]`. |

---

### TC-NOTIF-02: Filter Unread Notifications
**Description:** Verify filtering notifications by read status.
**Endpoints:** `GET /api/v1/notifications?unreadOnly=true`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint with `unreadOnly=true`. | 200 OK; Returns only notifications where `isRead` is false. |
| 2 | Verify that notifications where `isRead` is true are NOT included. | List contains only unread items. |

---

### TC-NOTIF-03: Mark Notification as Read
**Description:** Verify that a user can mark a specific notification as read.
**Endpoints:** `PATCH /api/v1/notifications/{id}/read`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint with a valid unread notification ID. | 200 OK; Notification status updated to `isRead = true`. |
| 2 | Call endpoint with an ID that is already read. | 200 OK; Status remains `isRead = true` (Idempotent). |
| 3 | Call endpoint with a non-existent ID. | 404 Not Found. |

---

### TC-NOTIF-04: Mark All as Read
**Description:** Verify that a user can mark all their notifications as read at once.
**Endpoints:** `PATCH /api/v1/notifications/read-all`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint for a user with multiple unread notifications. | 200 OK; All notifications for that user updated to `isRead = true`. |
| 2 | Verify that notifications belonging to OTHER users are NOT affected. | Isolation maintained. |

---

### TC-NOTIF-05: Security - Access Control
**Description:** Verify that a user cannot access or modify notifications belonging to others.
**Headers:** `Authorization: Bearer <user_a_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Attempt to mark User B's notification as read. | 403 Forbidden or 404 Not Found (to prevent ID enumeration). |
| 2 | Verify that User A's list only contains User A's notifications. | No leakage of data between users. |

---

### TC-NOTIF-06: Pagination and Sorting
**Description:** Verify that pagination works correctly for large numbers of notifications.
**Endpoints:** `GET /api/v1/notifications?page=0&size=5`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint with `size=5`. | 200 OK; Returns exactly 5 notifications (if available). |
| 2 | Verify `totalElements` and `totalPages` in the response metadata. | Correct counts returned. |
| 3 | Verify sorting is consistently `createdAt` DESC. | Most recent notifications appear first. |
