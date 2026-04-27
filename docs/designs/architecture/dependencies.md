# System Dependencies & Ecosystem

## Infrastructure Services (runtime)
| Service      | Purpose                                      | Docker Image              |
| ------------ | -------------------------------------------- | ------------------------- |
| PostgreSQL   | Relational metadata database                 | `postgres:16-alpine`      |
| MinIO        | S3‑compatible object storage                 | `minio/minio:latest`      |
| Redis        | Cache & token store                          | `redis:7-alpine`          |
| RabbitMQ     | Message broker for events                    | `rabbitmq:3-management`   |

## DevOps / Tooling
- **Docker Engine + Docker Compose** for local development and deployment.
- **Git** for version control.
- **Environment variables** (.env) manage secrets and configuration.

## Ecosystem Notes
- All services communicate over the Docker network.
- Backend depends on external services at startup; health checks are configured.
