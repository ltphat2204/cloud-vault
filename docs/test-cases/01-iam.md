# Test Cases: IAM Module

## Overview
These test cases verify the Identity and Access Management (IAM) module, focusing on registration, authentication, token rotation, and security linkage.

---

### TC-IAM-01: User Registration
**Description:** Verify that a new user can register successfully and duplicate emails are rejected.
**Endpoints:** `POST /api/v1/auth/register`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit valid `email`, `password`, and `name`. | 201 Created; User record saved in DB with `is_verified: false`; Password hashed; Verification email sent via RabbitMQ. |
| 2 | Submit registration with an email that already exists. | 409 Conflict; Error message "Email already exists". |
| 3 | Submit with missing required fields or invalid email format. | 400 Bad Request; Validation error details. |

---

### TC-IAM-02: User Login
**Description:** Verify that users can authenticate and receive a secure token pair.
**Endpoints:** `POST /api/v1/auth/login`
**Headers:** `X-Device-Id: <unique_id>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit valid credentials for a verified account with `X-Device-Id` header. | 200 OK; Access Token in body; Refresh Token in HttpOnly cookie. |
| 2 | Submit invalid password. | 401 Unauthorized; Error "Invalid email or password". |
| 3 | Submit valid credentials for an **unverified** account. | 403 Forbidden; Error "User account is not verified". |
| 4 | Submit without `X-Device-Id` header. | 400 Bad Request (Header required). |

---

### TC-IAM-03: Access Protected Resource
**Description:** Verify that the Access Token (AT) grants access to protected endpoints.
**Endpoints:** `GET /api/v1/auth/me`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint with a valid, non-expired AT. | 200 OK; Returns user profile data. |
| 2 | Call endpoint with an expired AT. | 401 Unauthorized. |
| 3 | Call endpoint without any token. | 401 Unauthorized. |

---

### TC-IAM-04: Token Refresh (Rotation & Triple-Link)
**Description:** Verify that tokens can be rotated and security linkage is enforced.
**Endpoints:** `POST /api/v1/auth/refresh`
**Headers:** `Authorization: Bearer <access_token>`, `X-Device-Id: <unique_id>`
**Cookie:** `refresh_token=<token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit valid AT, RT, and matching Device ID. | 200 OK; Old RT revoked; New AT and RT issued. |
| 2 | Submit a Refresh Token that has already been used. | 403 Forbidden (Token rotation violation). |
| 3 | Submit a valid RT but with a mismatched Access Token (Linkage Mismatch). | 403 Forbidden; **Critical**: All tokens for the user session are revoked in Redis. |
| 4 | Submit a valid RT and AT but with a different `X-Device-Id`. | 403 Forbidden (Device mismatch). |

---

### TC-IAM-05: User Logout
**Description:** Verify that tokens are invalidated upon logout.
**Endpoints:** `POST /api/v1/auth/logout`
**Headers:** `Authorization: Bearer <access_token>`
**Cookie:** `refresh_token=<token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call logout with valid tokens. | 200 OK; RT deleted from Redis; Cookie cleared (Max-Age=0). |
| 2 | Attempt to use the same RT for a refresh after logout. | 403 Forbidden. |

---

### TC-IAM-06: API Prefix Verification
**Description:** Verify that the global API prefix is correctly applied.

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call `GET /auth/me` (without `/api/v1`). | 404 Not Found. |
| 2 | Call `GET /api/v1/auth/me`. | Correct endpoint resolution. |

---

### TC-IAM-07: Account Verification (Email Token)
**Description:** Verify that an account can be activated using a valid verification token.
**Endpoints:** `POST /api/v1/auth/verify?token=<token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit a valid, non-expired token. | 200 OK; User status updated to `is_verified: true`. |
| 2 | Submit an expired token. | 400 Bad Request; Error "Token has expired". |
| 3 | Submit an invalid/non-existent token. | 400 Bad Request; Error "Invalid token". |

---

### TC-IAM-08: Forgot Password Flow
**Description:** Verify that a user can request a password reset and update their password using the token.
**Endpoints:** `POST /api/v1/auth/forgot-password`, `POST /api/v1/auth/reset-password`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Request reset for a verified email. | 200 OK; Reset email sent via RabbitMQ. |
| 2 | Request reset for an **unverified** email. | 403 Forbidden; Error "User account is not verified". |
| 3 | Reset password using valid token and new password. | 200 OK; Password hash updated in DB; Token invalidated. |
| 4 | Reset password using expired token. | 400 Bad Request; Error "Token has expired". |

---

### TC-IAM-09: Resend Verification Email
**Description:** Verify that a user can request a new verification email.
**Endpoints:** `POST /api/v1/auth/resend-verification?email=<email>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Request resend for an unverified account. | 200 OK; New verification email sent. |
| 2 | Request resend for an already verified account. | 400 Bad Request; Error "User is already verified". |
