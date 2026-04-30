# UC-24: Verify Email

**ID:** UC-24  
**Name:** Verify Email  
**Actors:** Unverified User  
**Preconditions:**
- User has successfully registered (UC-01).
- User has received a verification email.

**Postconditions:**
- User account is marked as verified.
- User can now use features requiring verification (e.g., Forgot Password).

**Main Flow:**
1. User receives an email with a verification link.
2. User clicks the verification link.
3. System extracts the verification token from the link.
4. System validates the token (exists, not expired, not already used).
5. System updates the user's status to "verified" in the database.
6. System returns a success message to the user.

**Alternative Flows:**
- **4a. Token expired:** System informs user and provides an option to resend the verification email.
- **4b. Token invalid/not found:** System returns an error message.

**Exceptions/Error Handling:**
- 400 – Invalid or expired token.
- 500 – Unexpected server error.

**Notes:**
- Verification tokens should have a limited lifespan (e.g., 24 hours).
- Once verified, the token is invalidated.
