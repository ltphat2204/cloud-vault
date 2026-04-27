# UC-06: Manage Project

**ID:** UC-06  
**Name:** Manage Project  
**Actors:** Project Owner  
**Preconditions:** User owns the project.  
**Postconditions:** Project is renamed or soft-deleted (moved to trash).  

**Main Flow (Rename):**
1. User selects project and chooses "Rename".
2. User enters new name.
3. System validates name and updates record.
4. System returns updated metadata.

**Main Flow (Delete):**
1. User selects project and chooses "Delete".
2. User confirms deletion.
3. System marks project and all nested contents as deleted (trash state).
4. System returns success.

**Alternative Flows:**
- **1a. User is not owner:** 403 Forbidden.

**Exceptions:** 401, 403, 404 (Project not found), 500.
