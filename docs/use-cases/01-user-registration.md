# UC-01: User Registration

**ID:** UC-01  
**Name:** User Registration  
**Actors:** Unregistered User  
**Preconditions:**
- User is not logged in.
- Email is not already registered.

**Postconditions:**
- A new user account is created.
- User receives a success message; optionally a verification email.

**Main Flow:**
1. User navigates to the registration page.
2. User enters email, password, and confirm password.
3. System validates input (valid email format, password strength, passwords match).
4. System checks that email is not already in use.
5. System hashes the password and creates a `User` record.
6. System returns a success response (no auto‑login).
7. System sends a verification email with a token (required for account activation and features like UC-04).

**Alternative Flows:**
- **2a. Invalid input:** show validation errors inline.
- **4a. Email already exists:** return conflict error (409).
- **5a. Database failure:** return internal error (500).

**Exceptions/Error Handling:**
- 400 – Validation errors (invalid email, weak password).
- 409 – Email already registered.
- 500 – Unexpected server error.

**Notes:**
- Passwords are stored using bcrypt.
- No auto‑login after registration; user must log in separately.
