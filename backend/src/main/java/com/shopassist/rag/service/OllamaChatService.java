package com.shopassist.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OllamaChatService {

    private final RestClient restClient;
    private final String model;

    public OllamaChatService(@Value("${app.ollama.base-url}") String baseUrl,
                             @Value("${app.ollama.chat-model}") String model) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.model = model;
    }

    @SuppressWarnings("unchecked")
    public String ask(String prompt) {
        try {
            Map<String, Object> response = restClient.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "model", model,
                            "stream", false,
                            "messages", List.of(Map.of("role", "user", "content", prompt))
                    ))
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> message = (Map<String, Object>) response.get("message");
            return message == null ? "" : String.valueOf(message.getOrDefault("content", ""));
        } catch (Exception e) {
            throw new IllegalStateException("Ollama chat error: " + e.getMessage(), e);
        }
    }
}
