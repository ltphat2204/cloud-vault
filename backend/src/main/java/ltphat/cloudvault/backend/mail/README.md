# Mail Module

The Mail module handles email delivery for the CloudVault application. It is designed to be generic and follows Clean Architecture principles, allowing other modules to trigger email notifications asynchronously via messaging.

## Features

- **Generic Email Delivery**: Supports sending both plain text and HTML emails.
- **Asynchronous Processing**: Uses RabbitMQ to process email requests in the background, ensuring core business logic is not blocked by email latency.
- **Caller-Rendered Templates**: The module focus is on delivery; the calling module is responsible for rendering the email body (text or HTML).
- **Retry Mechanism**: Leverages messaging infrastructure for robust delivery.

## Module Structure

The module is organized into layers following Clean Architecture:

- **application**: Contains service implementations.
- **domain**: Defines the core `MailRequest` model, `MailService` interface, and `MailSender` repository interface.
- **infrastructure**: Implements the `MailSender` via `JavaMailSender` and handles RabbitMQ message consumption.

## Key Components

- `MailRequest`: A domain model (record) encapsulating the recipient, subject, body, and HTML status.
- `MailService`: The interface used by the consumer to process email requests.
- `MailConsumer`: A RabbitMQ listener that receives `MailRequest` messages and delegates to the `MailService`.
- `JavaMailSenderAdapter`: An infrastructure adapter that implements the `MailSender` interface using Spring's `JavaMailSender`.

## Usage

To send an email from another module, publish a `MailRequest` to the configured RabbitMQ exchange/queue. The Mail module will automatically pick up the message and deliver the email.

Example `MailRequest`:
```java
MailRequest.builder()
    .to("user@example.com")
    .subject("Welcome to CloudVault")
    .body("Hello! Please verify your account...")
    .isHtml(false)
    .build();
```
