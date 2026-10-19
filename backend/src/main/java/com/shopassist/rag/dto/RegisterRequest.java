package com.shopassist.rag.dto;

public record RegisterRequest(
        String email,
        String password
) {}