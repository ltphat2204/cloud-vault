# IAM Module (Identity and Access Management)

The IAM module handles user authentication, authorization, and profile management for the CloudVault application. It follows Clean Architecture principles to ensure separation of concerns and maintainability.

## Features

- **User Registration**: Allows new users to create accounts.
- **User Login**: Secure authentication with support for device and IP address tracking for enhanced security.
- **Token Management**: JWT-based authentication with support for access and refresh tokens.
- **Logout**: Securely invalidates user sessions.
- **User Profile**: Retrieve the currently authenticated user's information.
- **Token Verification**: Endpoint to validate JWT tokens.

## Module Structure

The module is organized into four main layers following Clean Architecture:

- **presentation**: Contains REST controllers and request/response mapping logic.
- **application**: Defines use cases through services and data transfer objects (DTOs).
- **domain**: Contains core business logic, including entities and repository interfaces.
- **infrastructure**: Implements repository interfaces (e.g., JPA) and other external concerns.

## Key Components

- `AuthResult`: Encapsulates the results of a successful authentication, including tokens and user info.
- `IAuthService`: The primary interface defining authentication use cases.
- `LoginRequest`/`RegisterRequest`: DTOs for capturing user input.
