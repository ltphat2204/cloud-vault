# UC-08: Create Folder

**ID:** UC-08  
**Name:** Create Folder  
**Actors:** Authenticated User (with EDIT permission)  
**Preconditions:** User is logged in and has access to the parent project/folder.  
**Postconditions:** A new folder is created in the specified location.  

**Main Flow:**
1. User navigates to a project or folder.
2. User clicks "New Folder".
3. User enters folder name.
4. System validates name (e.g., uniqueness in current folder).
5. System creates `Folder` record with parent reference.
6. System returns folder metadata.

**Alternative Flows:**
- **4a. Name exists:** 409 Conflict error.

**Exceptions:** 401, 403, 404, 409, 500.
