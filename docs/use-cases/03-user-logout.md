# UC-03: User Logout

**ID:** UC-03  
**Name:** User Logout  
**Actors:** Authenticated User  
**Preconditions:** User holds valid refresh token.  
**Postconditions:** Refresh token is invalidated (blacklisted in Redis).  

**Main Flow:**
1. User clicks logout.
2. System receives refresh token.
3. System adds token to Redis blacklist with TTL matching token expiry.
4. System clears client-side tokens.

**Alternative Flows:** none.
**Exceptions:** Unauthorized if no token.
