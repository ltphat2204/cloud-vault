# UC-25: View Activity History

**ID:** UC-25  
**Name:** View Activity History  
**Actors:** Authenticated User  
**Preconditions:**
- User is logged in.

**Postconditions:**
- User sees a list of activities they have performed or that occurred on resources they own/have access to.

**Main Flow (Global History):**
1. User navigates to the "Activity" or "Audit" page.
2. System retrieves the latest activity logs for the user.
3. System returns the list sorted by date (descending).
4. User views the activity details (action, resource, date).

**Main Flow (Resource History):**
1. User selects a specific Project, Folder, or File.
2. User requests the "History" for that specific resource.
3. System verifies user's permission to view the resource history.
4. System retrieves and returns all activities related to that resource.

**Alternative Flows:**
- **3a. Permission Denied:** If the user does not have permission, the system returns an error (403).
- **2b. No Activities:** If no history exists, the system returns an empty list.

**Exceptions/Error Handling:**
- 401 – Unauthorized (not logged in).
- 403 – Forbidden (no access to resource history).
- 500 – Unexpected server error.

**Notes:**
- Activities include uploads, downloads, moves, deletions, and sharing events.
- Audit logs are immutable and cannot be deleted by users.
