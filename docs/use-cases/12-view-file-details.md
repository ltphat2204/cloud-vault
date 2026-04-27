# UC-12: View File Details

**ID:** UC-12  
**Name:** View File Details  
**Actors:** Authenticated User (with VIEW permission)  
**Preconditions:** User has access to the file.  
**Postconditions:** User sees detailed metadata and history.  

**Main Flow:**
1. User selects a file and chooses "Details" or "Properties".
2. System fetches metadata (name, size, type, owner, creation/update dates).
3. System fetches version history.
4. System fetches current share information.
5. Frontend displays details in a sidebar or dialog.

**Exceptions:** 401, 403, 404, 500.
