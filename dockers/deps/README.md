# CloudVault Infrastructure Dependencies

This directory contains the Docker Compose configuration to run the external dependencies required by the CloudVault system.

## Included Services

- **PostgreSQL (16)**: Primary relational database for metadata.
- **MinIO**: S3-compatible object storage for binary file content.
- **Redis (7)**: Distributed cache and session store.
- **RabbitMQ (3-management)**: Message broker for asynchronous event processing.

## Getting Started

### 1. Prerequisites
- Docker Engine installed.
- Docker Compose installed.

### 2. Setup Environment
Copy the example environment file to create your local configuration:

```bash
cp .env.example .env
```

### 3. Start Dependencies
Run the following command to start all services in the background:

```bash
docker compose up -d
```

### 4. Verify Health
You can check the status of the containers and their health checks:

```bash
docker compose ps
```

---

## Environment Variables Reference

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| **PostgreSQL** | | |
| `POSTGRES_USER` | Admin username for Postgres | `vault_user` |
| `POSTGRES_PASSWORD` | Admin password for Postgres | `vault_password` |
| `POSTGRES_DB` | Initial database name | `cloud_vault` |
| `POSTGRES_PORT` | External port for Postgres | `5432` |
| **MinIO** | | |
| `MINIO_ROOT_USER` | Root username for MinIO | `vault_minio_user` |
| `MINIO_ROOT_PASSWORD` | Root password for MinIO | `vault_minio_password` |
| `MINIO_PORT` | API port for MinIO | `9000` |
| `MINIO_CONSOLE_PORT` | Web Console port for MinIO | `9001` |
| **Redis** | | |
| `REDIS_PORT` | External port for Redis | `6379` |
| **RabbitMQ** | | |
| `RABBITMQ_USER` | Admin username for RabbitMQ | `vault_rabbit_user` |
| `RABBITMQ_PASSWORD` | Admin password for RabbitMQ | `vault_rabbit_password` |
| `RABBITMQ_PORT` | AMQP port for RabbitMQ | `5672` |
| `RABBITMQ_MGMT_PORT` | Management UI port for RabbitMQ | `15672` |

## Management Interfaces

Once started, you can access the following web interfaces:

- **MinIO Console**: [http://localhost:9001](http://localhost:9001)
- **RabbitMQ Management**: [http://localhost:15672](http://localhost:15672)
