# Shared Module

The Shared module contains common components, utilities, and configurations that are used across multiple feature modules in the CloudVault backend.

## Core Responsibilities

- **Security**: Provides JWT-based authentication components (providers, filters) and security configurations.
- **Exception Handling**: A centralized global exception handler that ensures consistent error responses across the API.
- **Configuration**: Shared configuration classes, such as OpenAPI/Swagger settings for API documentation.
- **Utilities**: Common helper classes for tasks like cookie management and string manipulation.
- **Common DTOs**: Base classes for standard API responses and shared data transfer objects.

## Package Breakdown

- `config`: Global application configurations (e.g., `OpenApiConfig`).
- `dto`: Shared data transfer objects used by multiple modules.
- `exception`: Custom exception classes and the `GlobalExceptionHandler`.
- `security`: JWT-related logic including `JwtTokenProvider` and `JwtAuthenticationFilter`.
- `utils`: Static utility classes (e.g., `CookieUtils`).

## Usage

Components in this module are intended to be imported and used by feature modules (like `iam` or `projects`) to avoid code duplication and maintain consistency across the system.
