package com.localhire.core.dto;

public record AuthResponse(
    String token,
    String email,
    String role,
    String fullName
) {}