# UC-02: User Login

**ID:** UC-02  
**Name:** User Login  
**Actors:** Registered User  
**Preconditions:** User has an active account.  
**Postconditions:** User receives access + refresh JWT tokens.  

**Main Flow:**
1. User submits email and password.
2. System validates credentials.
3. System checks if account is verified.
4. System generates JWT pair (access 15 min, refresh 7 days).
5. System returns tokens and user profile.

**Alternative Flows:**
- **2a. Invalid credentials:** 401 Unauthorized.
- **3a. Account not verified:** 403 Forbidden; User is prompted to verify email.

**Exceptions:** 401, 500.
