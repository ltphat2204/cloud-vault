# UC-04: Forgot Password / Reset

**ID:** UC-04  
**Name:** Forgot Password / Reset  
**Actors:** Unauthenticated User  
**Preconditions:** User has a registered and verified email address.  
**Postconditions:** User's password is updated.  

**Main Flow (Request Reset):**
1. User enters email on "Forgot Password" page.
2. System validates email existence.
3. System generates a one-time reset token with expiry.
4. System sends an email with a link containing the token.
5. System confirms request sent.

**Main Flow (Perform Reset):**
1. User clicks link in email.
2. User enters new password and confirms.
3. System validates token and password strength.
4. System updates user's password hash in database.
5. System invalidates the reset token.
6. System returns success message.

**Alternative Flows:**
- **2a. Email not found:** System returns success message (for security/privacy) but sends no email.
- **1b. Invalid/Expired token:** System returns error and prompts to request again.

**Exceptions:** 400 (Invalid token/password), 500.
