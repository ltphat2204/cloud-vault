# UC-15: Share Resource

**ID:** UC-15  
**Name:** Share Resource  
**Actors:** Owner of the Project/Folder/File  
**Preconditions:** User has permission to share the resource.  
**Postconditions:** Another user is granted access to the resource.  

**Main Flow:**
1. User selects a resource and chooses "Share".
2. User enters the email of the recipient and selects permission (VIEW/EDIT).
3. System validates recipient email existence.
4. System creates a `Share` record.
5. System publishes `ProjectSharedEvent` (or similar).
6. Recipient receives a notification.
7. System returns success.

**Alternative Flows:**
- **3a. Recipient not found:** System returns error or optionally allows inviting by email (future).

**Exceptions:** 401, 403, 404, 500.
