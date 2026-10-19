# How to run ShopAssist Full Stack

## Prerequisites
- Java 21
- Maven 3.9+
- Node.js 20+
- npm
- Docker Desktop
- Ollama

## 1) Start PostgreSQL with pgvector
From the project root:

```bash
docker compose up -d
```

This starts PostgreSQL on:
- host: `localhost`
- port: `5432`
- db: `shopassist`
- user: `shopassist`
- password: `shopassist`

## 2) Start Ollama and pull models
Make sure Ollama is installed and running locally on:
- `http://localhost:11434`

Pull models:

```bash
ollama pull llama3.1:8b
ollama pull nomic-embed-text
```

Quick checks:

```bash
curl http://localhost:11434/api/chat -d '{"model":"llama3.1:8b","stream":false,"messages":[{"role":"user","content":"Say hello"}]}'
```

```bash
curl http://localhost:11434/api/embeddings -d '{"model":"nomic-embed-text","prompt":"return policy electronics"}'
```

## 3) Run backend
Open a terminal in `backend/` and run:

```bash
mvn spring-boot:run
```

Backend runs on:
- `http://localhost:8081`

Swagger UI:
- `http://localhost:8081/swagger-ui/index.html`

## 4) Run frontend
Open a second terminal in `frontend/` and run:

```bash
npm install
npm start
```

Angular frontend runs on:
- `http://localhost:4200`

## 5) Demo login accounts
Seeded automatically by the backend:

- admin: `admin@demo.com` / `admin123`
- support: `support@demo.com` / `support123`
- customer: `customer@demo.com` / `customer123`

## 6) First demo flow
1. Log in as `admin@demo.com`
2. Open Upload page
3. Click `Bootstrap Demo Docs`
4. Go to Chat page
5. Ask: `What is the return policy for electronics?`
6. Go to Analytics page

## 7) Main API endpoints
- `POST /api/auth/login`
- `GET /api/health`
- `POST /api/ingest/bootstrap`
- `POST /api/ingest/upload`
- `POST /api/qa/ask`
- `GET /api/analytics/summary`

## Notes
- The backend automatically takes `tenantId` and `role` from the JWT token.
- The upload and analytics pages are protected by role.
- If your embedding dimension differs from `768`, update the Flyway migration `V1__init.sql` accordingly.
