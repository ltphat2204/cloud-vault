# UC-23: Configure Upload Limit

**ID:** UC-23  
**Name:** Configure Upload Limit  
**Actors:** Administrator  
**Preconditions:** User has ADMIN role.  
**Postconditions:** Global or per-user upload limits are updated.  

**Main Flow:**
1. Admin navigates to Admin Settings.
2. Admin enters new maximum file size (e.g., 500MB).
3. System updates configuration in database or environment-backed store.
4. System returns success.
5. Subsequent upload requests (UC-10) are validated against this new limit.

**Exceptions:** 401, 403 (Not Admin), 500.
