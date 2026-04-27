# UC-19: File Versioning

**ID:** UC-19  
**Name:** File Versioning  
**Actors:** Authenticated User (with VIEW permission)  
**Preconditions:** File has multiple versions or user wants to see history.  
**Postconditions:** User can view or restore previous versions.  

**Main Flow (View History):**
1. User selects a file and chooses "Version History".
2. System fetches all `FileVersion` records for the file.
3. System returns list with version number, size, and date.

**Main Flow (Restore):**
1. User selects a previous version and clicks "Restore".
2. System creates a new version by copying the data of the selected version.
3. System returns success.

**Exceptions:** 401, 403, 404, 500.
