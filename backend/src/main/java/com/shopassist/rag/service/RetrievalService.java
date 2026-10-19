package com.shopassist.rag.service;

import com.shopassist.rag.domain.SupportChunk;
import com.shopassist.rag.repository.ChunkRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RetrievalService {

    private final ChunkRepository chunkRepository;
    private final OllamaEmbeddingService embeddingService;
    private final int topKVector;
    private final int topKKeyword;
    private final int mergedLimit;

    public RetrievalService(ChunkRepository chunkRepository,
                            OllamaEmbeddingService embeddingService,
                            @Value("${app.retrieval.top-k-vector:5}") int topKVector,
                            @Value("${app.retrieval.top-k-keyword:5}") int topKKeyword,
                            @Value("${app.retrieval.merged-limit:6}") int mergedLimit) {
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
        this.topKVector = topKVector;
        this.topKKeyword = topKKeyword;
        this.mergedLimit = mergedLimit;
    }

    public List<SupportChunk> retrieve(String tenantId, String question) {
        float[] embedding = embeddingService.embed(question);
        List<SupportChunk> vectorHits = chunkRepository.vectorSearch(tenantId, embedding, topKVector);
        List<SupportChunk> keywordHits = chunkRepository.keywordSearch(tenantId, question, topKKeyword);

        Map<String, SupportChunk> merged = new LinkedHashMap<>();
        List<SupportChunk> combined = new ArrayList<>();
        combined.addAll(vectorHits);
        combined.addAll(keywordHits);

        for (SupportChunk hit : combined) {
            merged.merge(hit.id(), hit, (oldHit, newHit) -> oldHit.score() >= newHit.score() ? oldHit : newHit);
        }

        return merged.values().stream()
                .sorted(Comparator.comparingDouble(SupportChunk::score).reversed())
                .limit(mergedLimit)
                .toList();
    }
}
