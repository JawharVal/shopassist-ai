package com.shopassist.rag.service;

import com.shopassist.rag.domain.AskRequest;
import com.shopassist.rag.domain.AskResponse;
import com.shopassist.rag.domain.ChunkCitation;
import com.shopassist.rag.domain.SupportChunk;
import com.shopassist.rag.repository.AnalyticsRepository;
import com.shopassist.rag.security.AuthenticatedUser;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class AnswerService {

    private final RetrievalService retrievalService;
    private final OllamaChatService chatService;
    private final AnalyticsRepository analyticsRepository;
    private final MeterRegistry meterRegistry;

    public AnswerService(RetrievalService retrievalService,
                         OllamaChatService chatService,
                         AnalyticsRepository analyticsRepository,
                         MeterRegistry meterRegistry) {
        this.retrievalService = retrievalService;
        this.chatService = chatService;
        this.analyticsRepository = analyticsRepository;
        this.meterRegistry = meterRegistry;
    }

    public AskResponse ask(AuthenticatedUser user, AskRequest request) {
        long start = System.currentTimeMillis();
        List<SupportChunk> chunks = retrievalService.retrieve(user.tenantId(), request.question());

        String context = chunks.stream()
                .map(chunk -> "[" + chunk.id() + "] " + chunk.title() + "\n" + chunk.content())
                .reduce("", (a, b) -> a + "\n\n" + b);

        String prompt = """
                You are an e-commerce support assistant.
                The current user role is: %s.
                Answer using only the retrieved context.
                If the answer is not clearly supported by the context, say you are not confident and recommend contacting support.
                Keep the answer concise and practical.

                Customer tier: %s
                Question: %s

                Retrieved context:
                %s
                """.formatted(user.role(), request.customerTier() == null ? "unknown" : request.customerTier(), request.question(), context);

        String answer = chatService.ask(prompt);
        long latency = System.currentTimeMillis() - start;

        meterRegistry.timer("shopassist.qa.latency").record(Duration.ofMillis(latency));
        meterRegistry.counter("shopassist.qa.requests").increment();
        meterRegistry.summary("shopassist.qa.context_chunks").record(chunks.size());

        double confidence = chunks.isEmpty() ? 0.0 : Math.min(0.95, 0.5 + (chunks.getFirst().score() / 2.0));
        List<ChunkCitation> citations = chunks.stream()
                .map(chunk -> new ChunkCitation(chunk.documentId(), chunk.id(), chunk.title()))
                .toList();

        analyticsRepository.saveEvent(user.tenantId(), user.email(), user.role(), request.question(), answer, confidence, latency);
        return new AskResponse(answer, confidence, citations, latency);
    }
}
