package dev.bops.stockguard.stock.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        UUID productId,
        UUID userId,
        String movementType,
        int quantity,
        String reason,
        List<SerialNumberResponse> serialNumbers,
        Instant createdAt
) {}
