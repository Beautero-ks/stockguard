package dev.bops.stockguard.replenishment.application.dto;

import java.time.Instant;
import java.util.UUID;

public record AlertResponse(
        UUID id,
        UUID productId,
        String productName,
        int currentStock,
        int minThreshold,
        int maxThreshold,
        int suggestedQuantity,
        String status,
        Instant createdAt
) {}