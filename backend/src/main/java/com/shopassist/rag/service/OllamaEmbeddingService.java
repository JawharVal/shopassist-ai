package com.shopassist.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OllamaEmbeddingService {

    private final RestClient restClient;
    private final String model;

    public OllamaEmbeddingService(@Value("${app.ollama.base-url}") String baseUrl,
                                  @Value("${app.ollama.embedding-model}") String model) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.model = model;
    }

    @SuppressWarnings("unchecked")
    public float[] embed(String text) {
        try {
            Map<String, Object> response = restClient.post()
                    .uri("/api/embeddings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("model", model, "prompt", text))
                    .retrieve()
                    .body(Map.class);
            List<Number> embedding = (List<Number>) response.get("embedding");
            float[] vector = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                vector[i] = embedding.get(i).floatValue();
            }
            return vector;
        } catch (Exception e) {
            throw new IllegalStateException("Ollama embedding error: " + e.getMessage(), e);
        }
    }
}
