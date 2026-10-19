package com.shopassist.rag.user;

public record AppUser(
        Long id,
        String email,
        String passwordHash,
        String tenantId,
        String role
) {}
