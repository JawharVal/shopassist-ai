CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE support_document (
    id VARCHAR(100) PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    source_type VARCHAR(100) NOT NULL,
    source_uri VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE support_chunk (
    id VARCHAR(150) PRIMARY KEY,
    document_id VARCHAR(100) NOT NULL REFERENCES support_document(id) ON DELETE CASCADE,
    tenant_id VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    token_estimate INTEGER NOT NULL,
    embedding vector(768),
    search_vector tsvector,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_support_chunk_doc ON support_chunk(document_id);
CREATE INDEX idx_support_chunk_tenant ON support_chunk(tenant_id);
CREATE INDEX idx_support_chunk_search_vector ON support_chunk USING gin(search_vector);

CREATE TABLE qa_event (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(100) NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    user_role VARCHAR(50) NOT NULL,
    question TEXT NOT NULL,
    answer TEXT,
    confidence DOUBLE PRECISION NOT NULL,
    latency_ms BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_qa_event_tenant ON qa_event(tenant_id);
CREATE INDEX idx_qa_event_created_at ON qa_event(created_at);
