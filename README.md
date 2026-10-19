# ShopAssist AI: Multi-Tenant RAG Support Assistant

An AI customer-support assistant that answers shop-policy questions from a company's own documents (retrieval-augmented generation). It runs **fully locally** with [Ollama](https://ollama.com), so there is no external AI API and no data leaves your machine.

Built with **Spring Boot 3, PostgreSQL + pgvector, Ollama and Angular**.


## Features

- **Hybrid retrieval**: documents are split into chunks (650 characters, 80 overlap), embedded with `nomic-embed-text` (768 dimensions) and stored in PostgreSQL with pgvector. Every question runs a vector search and a full-text search, and the results are merged and ranked.
- **Grounded answers**: `llama3.1:8b` answers from the retrieved context only, and the response includes a confidence score and source citations.
- **Multi-tenant and secure**: Spring Security with JWT and BCrypt, three roles (`ADMIN`, `SUPPORT`, `CUSTOMER`). The tenant ID comes from the token, so uploads, searches and analytics are scoped per tenant.
- **Admin tools**: document upload, demo-document bootstrap, and an analytics dashboard (question volume, average confidence, average latency).
- **Angular frontend**: login, chat, upload and analytics pages, with route guards and an HTTP interceptor that attaches the JWT.
- **Engineering**: Flyway migrations, Docker Compose for the database, Actuator + Micrometer (Prometheus) metrics, Swagger UI, JUnit tests.

## Architecture

```
Angular (4200)  ──JWT──▶  Spring Boot API (8081)  ──▶  PostgreSQL + pgvector (5432)
                                   │
                                   └──▶  Ollama (11434): llama3.1:8b + nomic-embed-text
```

Question flow: embed the question → vector search + full-text search (filtered by tenant) → merge and rank chunks → build a grounded prompt → generate the answer → return answer, confidence and citations → log the event for analytics.

## Tech stack

| Layer | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot 3.4, Spring Security, JWT (jjwt), Flyway |
| Database | PostgreSQL 16 with pgvector (`pgvector/pgvector:pg16`) |
| AI | Ollama, `llama3.1:8b` (chat), `nomic-embed-text` (embeddings) |
| Frontend | Angular 18 (standalone components), TypeScript |
| Ops | Docker Compose, Actuator, Micrometer Prometheus, springdoc OpenAPI |

## Getting started

### Prerequisites

Java 21, Maven 3.9+, Node.js 20+, Docker Desktop, and Ollama.

### 1. Start the database

```bash
docker compose up -d
```

PostgreSQL runs on `localhost:5432` (db, user and password are all `shopassist`).

### 2. Start Ollama and pull the models

```bash
ollama pull llama3.1:8b
ollama pull nomic-embed-text
```

Ollama must be running on `http://localhost:11434`.

### 3. Run the backend

```bash
cd backend
mvn spring-boot:run
```

API: `http://localhost:8081` · Swagger UI: `http://localhost:8081/swagger-ui/index.html`

### 4. Run the frontend

```bash
cd frontend
npm install
npm start
```

App: `http://localhost:4200`

### 5. Try it

Log in with a demo account (seeded on first start):

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@demo.com` | `admin123` |
| Support | `support@demo.com` | `support123` |
| Customer | `customer@demo.com` | `customer123` |

1. Log in as `admin@demo.com`.
2. Open **Upload** and click **Bootstrap Demo Docs**.
3. Open **Chat** and ask: `What is the return policy for electronics?`
4. Open **Analytics** to see volume, confidence and latency.

These accounts are for local demo use only. Change them and set `APP_JWT_SECRET` before deploying anywhere.

## API

| Method | Endpoint | Access |
| --- | --- | --- |
| `POST` | `/api/auth/login` | Public |
| `POST` | `/api/auth/register` | Public (creates a `CUSTOMER`) |
| `GET` | `/api/health` | Public |
| `POST` | `/api/ingest/bootstrap` | `ADMIN`, `SUPPORT` |
| `POST` | `/api/ingest/upload` | `ADMIN`, `SUPPORT` |
| `POST` | `/api/qa/ask` | Any authenticated user |
| `GET` | `/api/analytics/summary` | `ADMIN` |

## Configuration

Set in `backend/src/main/resources/application.yml`; these can be overridden with environment variables:

| Variable | Default |
| --- | --- |
| `APP_JWT_SECRET` | development placeholder (always override) |
| `APP_OLLAMA_BASE_URL` | `http://localhost:11434` |
| `APP_OLLAMA_CHAT_MODEL` | `llama3.1:8b` |
| `APP_OLLAMA_EMBEDDING_MODEL` | `nomic-embed-text` |

If you use an embedding model with a different dimension than 768, update `vector(768)` in `V1__init.sql`.

## Project structure

```
.
├── backend/                 Spring Boot API
│   └── src/main/java/com/shopassist/rag/
│       ├── config/          Security and CORS
│       ├── controller/      REST endpoints
│       ├── security/        JWT filter and service
│       ├── service/         Chunking, embeddings, retrieval, answers, analytics
│       ├── repository/      JDBC repositories (pgvector queries)
│       └── user/            Users, roles, auth
├── frontend/                Angular app (login, chat, upload, analytics)
└── docker-compose.yml       PostgreSQL + pgvector
```



## Running the tests

```bash
cd backend
mvn test
```

## Roadmap

- Evaluation harness for retrieval and answer quality
- Grafana dashboards on top of the Prometheus metrics
- Per-tenant sign-up instead of the shared demo tenant
- PDF and DOCX ingestion

## License

MIT
