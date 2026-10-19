package com.shopassist.rag.domain;

import java.util.List;

public record AnalyticsSummary(
        long totalQuestions,
        double averageConfidence,
        double averageLatencyMs,
        List<QuestionCount> topQuestions
) {
    public record QuestionCount(String question, long count) {}
}
