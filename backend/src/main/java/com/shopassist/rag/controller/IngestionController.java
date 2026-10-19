package com.shopassist.rag.controller;

import com.shopassist.rag.domain.SupportDocument;
import com.shopassist.rag.domain.UploadDocumentRequest;
import com.shopassist.rag.security.AuthenticatedUser;
import com.shopassist.rag.security.SecurityUtils;
import com.shopassist.rag.service.BootstrapDocumentService;
import com.shopassist.rag.service.IngestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/ingest")
public class IngestionController {

    private final BootstrapDocumentService bootstrapDocumentService;
    private final IngestionService ingestionService;
    private final SecurityUtils securityUtils;

    public IngestionController(BootstrapDocumentService bootstrapDocumentService,
                               IngestionService ingestionService,
                               SecurityUtils securityUtils) {
        this.bootstrapDocumentService = bootstrapDocumentService;
        this.ingestionService = ingestionService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/bootstrap")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> bootstrap() {
        AuthenticatedUser user = securityUtils.currentUser();
        int count = bootstrapDocumentService.bootstrap(user.tenantId());
        return Map.of("status", "ok", "documentsLoaded", count, "tenantId", user.tenantId());
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> upload(@Valid @RequestBody UploadDocumentRequest request) {
        AuthenticatedUser user = securityUtils.currentUser();
        SupportDocument document = new SupportDocument(
                request.id(),
                user.tenantId(),
                request.title(),
                request.sourceType(),
                request.sourceUri(),
                LocalDateTime.now()
        );
        ingestionService.ingest(document, request.content());
        return Map.of("status", "ok", "documentId", request.id(), "tenantId", user.tenantId());
    }
}
