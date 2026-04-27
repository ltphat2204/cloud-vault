# UC-16: Manage Share Permissions

**ID:** UC-16  
**Name:** Manage Share Permissions  
**Actors:** Owner or Admin  
**Preconditions:** Resource is already shared.  
**Postconditions:** Share permissions are updated or revoked.  

**Main Flow (Update):**
1. User opens share dialog for a resource.
2. User changes permission (e.g., EDIT to VIEW) for a specific user.
3. System updates the `Share` record.
4. System returns success.

**Main Flow (Revoke):**
1. User clicks "Remove" next to a shared user.
2. System deletes the `Share` record.
3. System returns success.

**Exceptions:** 401, 403, 404, 500.
