# UC-18: View Notifications

**ID:** UC-18  
**Name:** View Notifications  
**Actors:** Authenticated User  
**Preconditions:** User is logged in.  
**Postconditions:** User sees a list of events related to their account/projects.  

**Main Flow:**
1. User clicks on the notification bell icon.
2. System fetches notifications for the user from the database.
3. User views the list (e.g., "User X shared project Y with you").
4. User clicks "Mark as Read" or "Mark all as Read".
5. System updates notification status.

**Real-time aspect:**
1. When a new notification is created, it is pushed via WebSocket to the active session.

**Exceptions:** 401, 500.
