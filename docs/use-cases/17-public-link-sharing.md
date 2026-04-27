# UC-17: Public Link Sharing

**ID:** UC-17  
**Name:** Public Link Sharing  
**Actors:** Resource Owner  
**Preconditions:** User owns the resource.  
**Postconditions:** A public URL is generated that allows access without login.  

**Main Flow:**
1. User selects resource and chooses "Get Public Link".
2. User optionally sets a password and expiry date.
3. System generates a unique token/link.
4. System saves public share configuration in database.
5. System returns the link to the user.

**Main Flow (Accessing):**
1. Recipient navigates to the public link.
2. System checks if password is required; if so, prompts user.
3. System validates token and expiry.
4. System provides access to the resource.

**Exceptions:** 401, 403, 404, 500.
