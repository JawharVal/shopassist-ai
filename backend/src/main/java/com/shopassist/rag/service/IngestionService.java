package com.shopassist.rag.service;

import com.shopassist.rag.domain.SupportDocument;
import com.shopassist.rag.repository.ChunkRepository;
import com.shopassist.rag.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngestionService {

    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final ChunkingService chunkingService;
    private final OllamaEmbeddingService embeddingService;

    public IngestionService(DocumentRepository documentRepository,
                            ChunkRepository chunkRepository,
                            ChunkingService chunkingService,
                            OllamaEmbeddingService embeddingService) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.chunkingService = chunkingService;
        this.embeddingService = embeddingService;
    }

    public void ingest(SupportDocument document, String content) {
        documentRepository.upsert(document);
        chunkRepository.deleteByDocumentId(document.id());
        List<String> chunks = chunkingService.chunk(content);
        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            float[] embedding = embeddingService.embed(chunk);
            chunkRepository.insert(
                    document.id() + "-chunk-" + i,
                    document.id(),
                    document.tenantId(),
                    document.title(),
                    chunk,
                    estimateTokens(chunk),
                    embedding
            );
        }
    }

    private int estimateTokens(String text) {
        return Math.max(1, text.length() / 4);
    }
}
