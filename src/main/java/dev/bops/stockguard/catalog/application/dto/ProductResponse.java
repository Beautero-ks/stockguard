package dev.bops.stockguard.catalog.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String reference,
        String name,
        String description,
        String location,
        int minStockThreshold,
        int maxStockThreshold,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}