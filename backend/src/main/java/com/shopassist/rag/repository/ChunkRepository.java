package com.shopassist.rag.repository;

import com.shopassist.rag.domain.SupportChunk;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ChunkRepository {

    private final JdbcTemplate jdbcTemplate;

    public ChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteByDocumentId(String documentId) {
        jdbcTemplate.update("delete from support_chunk where document_id = ?", documentId);
    }

    public void insert(String id, String documentId, String tenantId, String title, String content, int tokenEstimate, float[] embedding) {
        String vector = toPgVector(embedding);
        jdbcTemplate.update("""
                INSERT INTO support_chunk(id, document_id, tenant_id, title, content, token_estimate, embedding, search_vector)
                VALUES (?, ?, ?, ?, ?, ?, ?::vector, to_tsvector('english', ?))
                """, id, documentId, tenantId, title, content, tokenEstimate, vector, content);
    }

    public List<SupportChunk> vectorSearch(String tenantId, float[] embedding, int limit) {
        String vector = toPgVector(embedding);
        return jdbcTemplate.query("""
                SELECT id, document_id, tenant_id, title, content, token_estimate,
                       1 - (embedding <=> ?::vector) AS score,
                       created_at
                FROM support_chunk
                WHERE tenant_id = ?
                ORDER BY embedding <=> ?::vector
                LIMIT ?
                """, (rs, rowNum) -> map(rs.getString("id"), rs.getString("document_id"), rs.getString("tenant_id"), rs.getString("title"), rs.getString("content"), rs.getInt("token_estimate"), rs.getDouble("score"), rs.getTimestamp("created_at")), vector, tenantId, vector, limit);
    }

    public List<SupportChunk> keywordSearch(String tenantId, String query, int limit) {
        return jdbcTemplate.query("""
                SELECT id, document_id, tenant_id, title, content, token_estimate,
                       ts_rank(search_vector, plainto_tsquery('english', ?)) AS score,
                       created_at
                FROM support_chunk
                WHERE tenant_id = ?
                  AND search_vector @@ plainto_tsquery('english', ?)
                ORDER BY score DESC
                LIMIT ?
                """, (rs, rowNum) -> map(rs.getString("id"), rs.getString("document_id"), rs.getString("tenant_id"), rs.getString("title"), rs.getString("content"), rs.getInt("token_estimate"), rs.getDouble("score"), rs.getTimestamp("created_at")), query, tenantId, query, limit);
    }

    public long countByTenant(String tenantId) {
        Long count = jdbcTemplate.queryForObject("select count(*) from support_chunk where tenant_id = ?", Long.class, tenantId);
        return count == null ? 0L : count;
    }

    private SupportChunk map(String id, String documentId, String tenantId, String title, String content, int tokenEstimate, double score, Timestamp createdAt) {
        LocalDateTime ts = createdAt == null ? LocalDateTime.now() : createdAt.toLocalDateTime();
        return new SupportChunk(id, documentId, tenantId, title, content, tokenEstimate, score, ts);
    }

    private String toPgVector(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(vector[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}
