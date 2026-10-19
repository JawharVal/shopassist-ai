package com.shopassist.rag.security;

public record AuthenticatedUser(String email, String tenantId, String role) {}
