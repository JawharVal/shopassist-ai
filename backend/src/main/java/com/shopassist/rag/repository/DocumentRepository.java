package com.shopassist.rag.repository;

import com.shopassist.rag.domain.SupportDocument;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DocumentRepository {

    private final JdbcClient jdbcClient;

    public DocumentRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void upsert(SupportDocument document) {
        jdbcClient.sql("""
                INSERT INTO support_document(id, tenant_id, title, source_type, source_uri, created_at)
                VALUES (:id, :tenantId, :title, :sourceType, :sourceUri, :createdAt)
                ON CONFLICT (id) DO UPDATE SET
                  tenant_id = EXCLUDED.tenant_id,
                  title = EXCLUDED.title,
                  source_type = EXCLUDED.source_type,
                  source_uri = EXCLUDED.source_uri
                """)
                .param("id", document.id())
                .param("tenantId", document.tenantId())
                .param("title", document.title())
                .param("sourceType", document.sourceType())
                .param("sourceUri", document.sourceUri())
                .param("createdAt", document.createdAt() == null ? LocalDateTime.now() : document.createdAt())
                .update();
    }

    public long countByTenant(String tenantId) {
        Long count = jdbcClient.sql("select count(*) from support_document where tenant_id = :tenantId")
                .param("tenantId", tenantId)
                .query(Long.class)
                .single();
        return count == null ? 0L : count;
    }

    public List<SupportDocument> findAllByTenant(String tenantId) {
        return jdbcClient.sql("select id, tenant_id, title, source_type, source_uri, created_at from support_document where tenant_id = :tenantId order by created_at desc")
                .param("tenantId", tenantId)
                .query((rs, rowNum) -> new SupportDocument(
                        rs.getString("id"),
                        rs.getString("tenant_id"),
                        rs.getString("title"),
                        rs.getString("source_type"),
                        rs.getString("source_uri"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                )).list();
    }
}
