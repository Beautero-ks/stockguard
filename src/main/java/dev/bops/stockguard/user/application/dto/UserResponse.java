package dev.bops.stockguard.user.application.dto;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String role,
        boolean enabled
) {}