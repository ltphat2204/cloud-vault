# CloudVault – System Architecture Overview

## Summary
CloudVault is a multi‑user cloud storage and sharing platform.  
The system follows **Clean Architecture with Domain‑Driven Design** on the backend, a **React SPA** on the frontend, and relies on a set of **infrastructure services** orchestrated via **Docker**.

## Core Components

| Layer            | Technology                                      | Role                                                       |
| ---------------- | ----------------------------------------------- | ---------------------------------------------------------- |
| **Frontend**     | React + Vite, TypeScript, Tailwind + shadcn/ui  | User interface, state management, real‑time updates        |
| **Backend**      | Java 21 + Spring Boot 3                         | REST API, business logic, authentication, event publishing |
| **Database**     | PostgreSQL                                      | Metadata (users, projects, files, shares, versions)        |
| **Object Store** | MinIO                                           | Binary file content (S3‑compatible)                        |
| **Cache**        | Redis                                           | Frequent metadata, session/refresh tokens, file lists      |
| **Message Broker** | RabbitMQ                                      | Async event processing (thumbnails, notifications, indexing) |
| **Real‑time**    | WebSocket                                       | Push notifications, upload progress, live updates          |
| **DevOps**       | Docker, Docker Compose                         | Containerised development and deployment                   |

## Ecosystem & External Dependencies
- **Backend**: Java, Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, PostgreSQL JDBC, MinIO client, Redis client, RabbitMQ client, SLF4J/Logback, Springdoc OpenAPI, **JUnit 5, Mockito, MockMvc**.
- **Frontend**: React 19, Vite, Axios/fetch, Zustand, TanStack Query, TanStack Router, Tailwind CSS, shadcn/ui components.
- **Infrastructure**: MinIO server, PostgreSQL, Redis, RabbitMQ – all containerised.
- **Build & Orchestration**: Docker Engine, Docker Compose, `.env` for configuration.

## Key Architectural Decisions
- **Layered separation** prevents framework coupling; domain layer is pure Java with no Spring annotations.
- **Event‑driven design** offloads non‑critical work to consumers (thumbnails, search indexing, notifications).
- **Metadata vs. object storage split** allows independent scaling.
- **Real‑time** via WebSocket possible for bidirectional.
