package com.shopassist.rag.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChunkingServiceTest {

    @Test
    void shouldSplitLongTextIntoMultipleChunks() {
        ChunkingService service = new ChunkingService(20, 5);
        var chunks = service.chunk("abcdefghijklmnopqrstuvwxyz0123456789");
        assertFalse(chunks.isEmpty());
        assertTrue(chunks.size() > 1);
    }
}
