# UC-10: Upload Files

**ID:** UC-10  
**Name:** Upload Files  
**Actors:** Authenticated User (with EDIT permission)  
**Preconditions:** User has edit access to the destination folder and sufficient quota.  
**Postconditions:** Files are stored in object storage and metadata is recorded.  

**Main Flow:**
1. User drags and drops files or selects them via dialog.
2. Frontend uses WebSocket connection for progress updates.
3. Frontend sends files via multipart POST.
4. System validates file size/type and storage quota.
5. System streams file content to MinIO.
6. System saves `File` metadata with a new `FileVersion`.
7. System publishes `FileUploadedEvent`.
8. System returns success.

**Alternative Flows:**
- **4a. Quota exceeded:** System returns 402 Payment Required or 413 Payload Too Large.
- **5a. Upload failure:** System returns 500 and cleans up partial data.

**Exceptions:** 401, 403, 413, 500.
