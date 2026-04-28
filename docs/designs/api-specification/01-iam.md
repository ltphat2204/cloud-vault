# IAM API Specification

Identity and Access Management APIs handle user registration, authentication, and session management.

## Endpoints

### 1. Register User
Creates a new user account.

- **URL**: `POST /auth/register`
- **Auth required**: No
- **Request Body (`RegisterRequest`)**:
  ```json
  {
    "email": "user@example.com",
    "password": "securePassword123",
    "name": "John Doe"
  }
  ```
- **Success Response**: `201 Created`
  ```json
  {
    "success": true,
    "message": "User registered successfully",
    "data": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com",
      "name": "John Doe"
    }
  }
  ```

---

### 2. Login
Authenticates a user and establishes a session. Returns an access token and sets a refresh token in a secure cookie.

- **URL**: `POST /auth/login`
- **Auth required**: No
- **Request Headers**:
  - `X-Device-Id` (String, Required): Unique ID for the device.
- **Request Body (`LoginRequest`)**:
  ```json
  {
    "email": "user@example.com",
    "password": "securePassword123"
  }
  ```
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Login successful",
    "data": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "user": {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "email": "user@example.com",
        "name": "John Doe"
      }
    }
  }
  ```
- **Cookies**:
  - `refreshToken`: HttpOnly cookie containing the refresh token.

---

### 3. Refresh Token
Refreshes the access token using a valid refresh token cookie.

- **URL**: `POST /auth/refresh`
- **Auth required**: Yes (Expired Access Token)
- **Request Headers**:
  - `Authorization`: `Bearer <expired_access_token>`
  - `X-Device-Id`: Unique ID for the device.
- **Cookies**:
  - `refreshToken` (Required): Valid refresh token.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Token refreshed successfully",
    "data": {
      "accessToken": "new_eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "user": {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "email": "user@example.com",
        "name": "John Doe"
      }
    }
  }
  ```

---

### 4. Logout
Terminates the current session and clears cookies.

- **URL**: `POST /auth/logout`
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Logout successful"
  }
  ```

---

### 5. Get Current User
Retrieves the profile of the currently authenticated user.

- **URL**: `GET /auth/me`
- **Auth required**: Yes
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "data": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com",
      "name": "John Doe"
    }
  }
  ```

---

### 6. Verify Email/Token
Verifies a user account using a token.

- **URL**: `POST /auth/verify`
- **Auth required**: No
- **Query Parameters**:
  - `token` (String, Required): Verification token.
- **Success Response**: `200 OK`
  ```json
  {
    "success": true,
    "message": "Email verified successfully"
  }
  ```

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
