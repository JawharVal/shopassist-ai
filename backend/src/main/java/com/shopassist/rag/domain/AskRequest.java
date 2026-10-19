package com.shopassist.rag.domain;

import jakarta.validation.constraints.NotBlank;

public record AskRequest(
        @NotBlank String question,
        String customerTier
) {}
