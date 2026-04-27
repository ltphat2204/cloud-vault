# UC-13: Trash Management

**ID:** UC-13  
**Name:** Trash Management  
**Actors:** Project Owner  
**Preconditions:** Items have been soft-deleted.  
**Postconditions:** Items are either restored or permanently removed.  

**Main Flow (Restore):**
1. User navigates to Trash view.
2. User selects items and clicks "Restore".
3. System removes deleted flag and ensures parent still exists (else moves to root).
4. System returns success.

**Main Flow (Permanent Delete):**
1. User selects items in Trash and clicks "Delete Permanently".
2. System deletes file content from MinIO and metadata from database.
3. System returns success.

**Alternative Flows:**
- **Empty Trash:** User clicks "Empty Trash" to delete all items.

**Exceptions:** 401, 403, 500.
