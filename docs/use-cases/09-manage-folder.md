# UC-09: Manage Folder

**ID:** UC-09  
**Name:** Manage Folder  
**Actors:** Authenticated User (with EDIT permission)  
**Preconditions:** User has edit access to the folder.  
**Postconditions:** Folder is renamed, moved, or soft-deleted.  

**Main Flow (Rename):**
1. User selects folder and chooses "Rename".
2. User enters new name.
3. System validates name uniqueness and updates record.
4. System returns success.

**Main Flow (Move):**
1. User selects folder and chooses "Move".
2. User selects destination folder/project.
3. System validates destination permissions and hierarchy (no circularity).
4. System updates parent reference.
5. System returns success.

**Main Flow (Delete):**
1. User selects folder and chooses "Delete".
2. System marks folder and nested contents as deleted.
3. System returns success.

**Exceptions:** 401, 403, 404, 409, 500.
