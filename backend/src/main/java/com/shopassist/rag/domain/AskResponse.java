package com.shopassist.rag.domain;

import java.util.List;

public record AskResponse(
        String answer,
        double confidence,
        List<ChunkCitation> citations,
        long latencyMs
) {}
