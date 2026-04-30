# UC-11: Download Files

**ID:** UC-11  
**Name:** Download Files  
**Actors:** Authenticated User (with VIEW permission)  
**Preconditions:** User has access to the files.  
**Postconditions:** User receives the file content.  

**Main Flow:**
1. User selects a single file or a folder.
2. User clicks the "Download" button.
3. System checks user's VIEW permission on the resource.
4. For a single file: System retrieves the file from storage and streams it to the user.
5. For a folder: System recursively collects all contents and streams them as a ZIP archive.

**Exceptions:** 401, 403, 404, 500.
