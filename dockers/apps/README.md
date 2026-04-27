# CloudVault Full Application Stack

This directory contains the Docker Compose configuration to run the entire CloudVault ecosystem, including the Backend (Spring Boot), Frontend (React), and all infrastructure dependencies.

## Architecture Overview

The stack consists of the following services:

- **Frontend**: React SPA served by Nginx.
- **Backend**: Spring Boot 3 API (Java 21).
- **PostgreSQL**: Metadata storage.
- **MinIO**: S3-compatible object storage for files.
- **Redis**: Caching and session management.
- **RabbitMQ**: Asynchronous event processing.

## Getting Started

### 1. Prerequisites
- Docker Engine 20.10+ 
- Docker Compose v2.0+

### 2. Setup Environment
Copy the example environment file:

```bash
cp .env.example .env
```

Review the `.env` file to ensure the ports and credentials match your requirements.

### 3. Build and Start
Run the following command to build the application images and start all services:

```bash
docker compose up --build -d
```

### 4. Access the Application
Once the services are healthy, you can access them at:

- **Frontend**: [http://localhost:3000](http://localhost:3000) (default)
- **Backend API**: [http://localhost:8080/api/v1](http://localhost:8080/api/v1)
- **API Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Service Endpoints

| Service | Host Port | Internal Port | Description |
| :--- | :--- | :--- | :--- |
| **Frontend** | `3000` | `80` | Web User Interface |
| **Backend API** | `3000/api/v1` | `8080` | REST API (Proxied) |
| **PostgreSQL** | `5432` | `5432` | Database |
| **MinIO API** | `9000` | `9000` | Object Storage API |
| **MinIO Console** | `9001` | `9001` | Storage Management UI |
| **RabbitMQ AMQP** | `5672` | `5672` | Message Broker |
| **RabbitMQ Mgmt** | `15672` | `15672` | Broker Management UI |
| **Redis** | `6379` | `6379` | Cache / Session Store |

---

## Environment Variables Reference

### Application Ports
- `BACKEND_PORT`: Port for the Spring Boot API (default: `8080`).
- `FRONTEND_PORT`: Port for the React UI (default: `3000`).

### Infrastructure Credentials
Refer to the [Infrastructure Dependencies README](../deps/README.md) for a detailed breakdown of database and broker variables. All variables in the `.env` file are automatically mapped to the appropriate application configurations inside the containers.

## Operational Commands

**View Logs:**
```bash
docker compose logs -f [service_name]
```

**Restart Applications Only:**
```bash
docker compose restart backend frontend
```

**Stop and Remove Containers:**
```bash
docker compose down
```

**Stop and Remove Everything (including volumes):**
```bash
docker compose down -v
```
