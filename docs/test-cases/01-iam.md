# Test Cases: IAM Module

## Overview
These test cases verify the Identity and Access Management (IAM) module, focusing on registration, authentication, token rotation, and security linkage.

---

### TC-IAM-01: User Registration
**Description:** Verify that a new user can register successfully and duplicate emails are rejected.
**Endpoints:** `POST /api/v1/auth/register`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit valid `email`, `password`, and `name`. | 201 Created; User record saved in DB; Password hashed. |
| 2 | Submit registration with an email that already exists. | 409 Conflict; Error message "Email already exists". |
| 3 | Submit with missing required fields or invalid email format. | 400 Bad Request; Validation error details. |

---

### TC-IAM-02: User Login
**Description:** Verify that users can authenticate and receive a secure token pair.
**Endpoints:** `POST /api/v1/auth/login`
**Headers:** `X-Device-Id: <unique_id>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Submit valid credentials with `X-Device-Id` header. | 200 OK; Access Token in body; Refresh Token in HttpOnly cookie. |
| 2 | Submit invalid password. | 401 Unauthorized; Error "Invalid email or password". |
| 3 | Submit without `X-Device-Id` header. | 400 Bad Request (Header required). |

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

### TC-IAM-07: Token Verification
**Description:** Verify that the system can validate a JWT token without returning full user profile.
**Endpoints:** `GET /api/v1/auth/verify`
**Headers:** `Authorization: Bearer <access_token>`

| Step | Action | Expected Result |
| :--- | :--- | :--- |
| 1 | Call endpoint with a valid AT. | 200 OK; Returns boolean `valid: true`. |
| 2 | Call endpoint with an invalid or expired AT. | 401 Unauthorized or 200 OK with `valid: false` (depending on implementation). |
