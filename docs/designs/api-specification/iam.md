# IAM API Specification

Identity and Access Management APIs handle user registration, authentication, and session management.

## Endpoints

### 1. Register User
`POST /auth/register`

Creates a new user account.

**Request Body (`RegisterRequest`):**
- `email` (String): User's unique email address.
- `password` (String): Secure password.
- `name` (String): User's full name.

**Response Data (`UserDto`):**
- `id` (UUID): Unique user identifier.
- `email` (String): User's email.
- `name` (String): User's name.

---

### 2. Login
`POST /auth/login`

Authenticates a user and establishes a session.

**Request Headers:**
- `X-Device-Id` (String, Required): Unique ID for the device.

**Request Body (`LoginRequest`):**
- `email` (String): User's email.
- `password` (String): User's password.

**Response Data (`AuthResponse`):**
- `accessToken` (String): JWT access token for subsequent requests.
- `user` (`UserDto`): User profile information.

**Cookies:**
- `refreshToken`: HttpOnly cookie containing the refresh token.

---

### 3. Refresh Token
`POST /auth/refresh`

Refreshes the access token using a valid refresh token cookie.

**Request Headers:**
- `Authorization` (String, Required): `Bearer <expired_access_token>`
- `X-Device-Id` (String, Required): Unique ID for the device.

**Cookies:**
- `refreshToken` (Required): Valid refresh token.

**Response Data (`AuthResponse`):** Same as Login.

---

### 4. Logout
`POST /auth/logout`

Terminates the current session and clears cookies.

---

### 5. Get Current User
`GET /auth/me`

Retrieves the profile of the currently authenticated user.

**Response Data (`UserDto`):** User profile information.

---

### 6. Verify Email/Token
`POST /auth/verify`

Verifies a user account using a token.

**Query Parameters:**
- `token` (String, Required): Verification token.

## Schemas

### UserDto
| Field | Type | Description |
| --- | --- | --- |
| `id` | UUID | Unique identifier |
| `email` | String | User email |
| `name` | String | User full name |

### AuthResponse
| Field | Type | Description |
| --- | --- | --- |
| `accessToken` | String | Short-lived JWT access token |
| `user` | UserDto | User profile data |
