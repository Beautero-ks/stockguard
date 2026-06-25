package dev.bops.stockguard.stock.domain.event;

import java.time.Instant;
import java.util.UUID;

public record StockExitCreatedEvent(
        UUID movementId,
        UUID productId,
        UUID userId,
        String serialNumber,
        UUID clientId,
        Instant occurredAt
) {
    public StockExitCreatedEvent {
        occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }
}