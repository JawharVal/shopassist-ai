package com.shopassist.rag.domain;

import java.time.LocalDateTime;

public record SupportDocument(
        String id,
        String tenantId,
        String title,
        String sourceType,
        String sourceUri,
        LocalDateTime createdAt
) {}
