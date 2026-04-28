# Test Cases: Trash Management

## TC-TRASH-01: List Trash Items
- **Preconditions**: User has soft-deleted 2 files and 1 folder.
- **Action**: Call `GET /api/v1/trash`.
- **Expected Result**: 
    - HTTP 200 OK.
    - List contains 3 items.
    - Each item has correct metadata (name, type, deletedAt).

## TC-TRASH-02: Restore a File
- **Preconditions**: User has 1 file in trash.
- **Action**: Call `POST /api/v1/trash/restore` with file ID.
- **Expected Result**:
    - HTTP 200 OK.
    - File `deletedAt` is null.
    - File appears in its original folder.

## TC-TRASH-03: Restore a Folder (Recursive)
- **Preconditions**: Folder containing 2 files was soft-deleted.
- **Action**: Call `POST /api/v1/trash/restore` with folder ID.
- **Expected Result**:
    - HTTP 200 OK.
    - Folder and its 2 files `deletedAt` are null.
    - Folder appears in its original location.

## TC-TRASH-04: Restore Folder with Deleted Parent
- **Preconditions**: Folder `Sub` was in `Parent`. Both are in trash.
- **Action**: Call `POST /api/v1/trash/restore` with `Sub` ID only.
- **Expected Result**:
    - HTTP 200 OK.
    - `Sub` `deletedAt` is null.
    - `Sub` `parentFolderId` is null (moved to root).

## TC-TRASH-05: Permanent Delete a File
- **Preconditions**: 1 file in trash.
- **Action**: Call `DELETE /api/v1/trash` with file ID.
- **Expected Result**:
    - HTTP 204 No Content.
    - File record is gone from DB.
    - File content is deleted from MinIO.

## TC-TRASH-06: Empty Trash
- **Preconditions**: User has multiple files and folders in trash.
- **Action**: Call `DELETE /api/v1/trash/empty`.
- **Expected Result**:
    - HTTP 204 No Content.
    - `GET /api/v1/trash` returns empty list.
    - All related MinIO objects are deleted.

## TC-TRASH-07: Recover All
- **Preconditions**: User has multiple files and folders in trash.
- **Action**: Call `POST /api/v1/trash/recover-all`.
- **Expected Result**:
    - HTTP 200 OK.
    - `GET /api/v1/trash` returns empty list.
    - All items are restored to their locations (or root if parent is missing).

---

## TC-TRASH-08: Batch Restore
- **Preconditions**: User has 5 items in trash.
- **Action**: Call `POST /api/v1/trash/restore/batch` with a list of 3 IDs.
- **Expected Result**:
    - HTTP 200 OK.
    - The 3 specified items are restored.
    - The other 2 items remain in trash.

---

## TC-TRASH-09: Batch Permanent Delete
- **Preconditions**: User has 5 items in trash.
- **Action**: Call `DELETE /api/v1/trash/batch` with a list of 3 IDs.
- **Expected Result**:
    - HTTP 204 No Content.
    - The 3 specified items are permanently deleted.
    - The other 2 items remain in trash.

---

## TC-TRASH-10: Restore Project
- **Preconditions**: A project (including its root folder and contents) was soft-deleted.
- **Action**: Call `POST /api/v1/trash/restore` with the project ID.
- **Expected Result**:
    - HTTP 200 OK.
    - Project `deletedAt` is null.
    - Project appears in project list.
    - All folders and files within the project that were deleted with it are also restored (recursive restoration).
