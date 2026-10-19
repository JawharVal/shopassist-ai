package com.shopassist.rag.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopassist.rag.domain.SupportDocument;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class BootstrapDocumentService {

    private final ObjectMapper objectMapper;
    private final IngestionService ingestionService;

    public BootstrapDocumentService(ObjectMapper objectMapper, IngestionService ingestionService) {
        this.objectMapper = objectMapper;
        this.ingestionService = ingestionService;
    }

    public int bootstrap(String tenantId) {
        try (InputStream inputStream = new ClassPathResource("bootstrap-docs/policies.json").getInputStream()) {
            List<Map<String, String>> docs = objectMapper.readValue(inputStream, new TypeReference<>() {});
            for (Map<String, String> doc : docs) {
                ingestionService.ingest(
                        new SupportDocument(
                                tenantId + "-" + doc.get("id"),
                                tenantId,
                                doc.get("title"),
                                doc.get("sourceType"),
                                doc.get("sourceUri"),
                                LocalDateTime.now()
                        ),
                        doc.get("content")
                );
            }
            return docs.size();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to bootstrap documents", e);
        }
    }
}
