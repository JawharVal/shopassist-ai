package com.shopassist.rag.repository;

import com.shopassist.rag.domain.AnalyticsSummary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnalyticsRepository {

    private final JdbcClient jdbcClient;

    public AnalyticsRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void saveEvent(String tenantId, String userEmail, String userRole, String question, String answer, double confidence, long latencyMs) {
        jdbcClient.sql("""
                INSERT INTO qa_event(tenant_id, user_email, user_role, question, answer, confidence, latency_ms)
                VALUES (:tenantId, :userEmail, :userRole, :question, :answer, :confidence, :latencyMs)
                """)
                .param("tenantId", tenantId)
                .param("userEmail", userEmail)
                .param("userRole", userRole)
                .param("question", question)
                .param("answer", answer)
                .param("confidence", confidence)
                .param("latencyMs", latencyMs)
                .update();
    }

    public AnalyticsSummary summary(String tenantId) {
        SummaryRow row = jdbcClient.sql("""
                SELECT COUNT(*) AS total_questions,
                       COALESCE(AVG(confidence), 0) AS average_confidence,
                       COALESCE(AVG(latency_ms), 0) AS average_latency_ms
                FROM qa_event
                WHERE tenant_id = :tenantId
                """)
                .param("tenantId", tenantId)
                .query((rs, i) -> new SummaryRow(rs.getLong("total_questions"), rs.getDouble("average_confidence"), rs.getDouble("average_latency_ms")))
                .single();

        List<AnalyticsSummary.QuestionCount> top = jdbcClient.sql("""
                SELECT question, COUNT(*) AS cnt
                FROM qa_event
                WHERE tenant_id = :tenantId
                GROUP BY question
                ORDER BY cnt DESC, question ASC
                LIMIT 10
                """)
                .param("tenantId", tenantId)
                .query((rs, i) -> new AnalyticsSummary.QuestionCount(rs.getString("question"), rs.getLong("cnt")))
                .list();

        return new AnalyticsSummary(row.totalQuestions(), row.averageConfidence(), row.averageLatencyMs(), top);
    }

    private record SummaryRow(long totalQuestions, double averageConfidence, double averageLatencyMs) {}
}
