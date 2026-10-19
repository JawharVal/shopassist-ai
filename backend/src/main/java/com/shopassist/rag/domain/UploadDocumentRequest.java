package com.shopassist.rag.domain;

import jakarta.validation.constraints.NotBlank;

public record UploadDocumentRequest(
        @NotBlank String id,
        @NotBlank String title,
        @NotBlank String sourceType,
        String sourceUri,
        @NotBlank String content
) {}
