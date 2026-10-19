package com.shopassist.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkingService {

    private final int chunkSize;
    private final int overlap;

    public ChunkingService(@Value("${app.chunking.chunk-size:650}") int chunkSize,
                           @Value("${app.chunking.overlap:80}") int overlap) {
        this.chunkSize = chunkSize;
        this.overlap = overlap;
    }

    public List<String> chunk(String content) {
        List<String> chunks = new ArrayList<>();
        if (content == null || content.isBlank()) {
            return chunks;
        }
        int start = 0;
        while (start < content.length()) {
            int end = Math.min(content.length(), start + chunkSize);
            chunks.add(content.substring(start, end));
            if (end == content.length()) {
                break;
            }
            start = Math.max(0, end - overlap);
        }
        return chunks;
    }
}
