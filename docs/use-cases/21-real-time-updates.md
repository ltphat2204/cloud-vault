# UC-21: Real‑Time Updates

**ID:** UC-21  
**Name:** Real‑Time Updates  
**Actors:** Authenticated User  
**Preconditions:** User has an active browser session and WebSocket connection.  
**Postconditions:** UI stays in sync with backend changes without page refreshes.  

**Main Flow:**
1. User logs in; Frontend establishes WebSocket connection.
2. Backend event occurs (e.g., another user uploads a file to a shared project).
3. `NotificationEmitterService` identifies active connections interested in this event.
4. System pushes event data through WebSocket.
5. Frontend receives event and triggers TanStack Query invalidation for affected data.
6. UI refreshes automatically.

**Exceptions:** 401, 500.
