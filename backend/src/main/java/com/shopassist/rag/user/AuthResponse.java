package com.shopassist.rag.user;

public record AuthResponse(String token, String email, String tenantId, String role) {}
