package com.shopassist.rag.dto;

public record UserResponse(
        Long id,
        String email,
        String tenantId,
        String role
) {}