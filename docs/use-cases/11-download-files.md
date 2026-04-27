# UC-11: Download Files

**ID:** UC-11  
**Name:** Download Files  
**Actors:** Authenticated User (with VIEW permission)  
**Preconditions:** User has access to the files.  
**Postconditions:** User receives the file content.  

**Main Flow (Single File):**
1. User clicks "Download" on a file.
2. System generates a temporary signed URL for MinIO or proxies the stream.
3. Frontend triggers browser download.

**Main Flow (Multiple Files/Folders):**
1. User selects multiple items and clicks "Download".
2. System gathers all file metadata.
3. System creates a ZIP stream of the requested items.
4. System returns the ZIP stream to the browser.

**Exceptions:** 401, 403, 404, 500.
