package com.shopassist.rag.domain;

import java.time.LocalDateTime;

public record SupportChunk(
        String id,
        String documentId,
        String tenantId,
        String title,
        String content,
        int tokenEstimate,
        double score,
        LocalDateTime createdAt
) {}
