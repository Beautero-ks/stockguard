package dev.bops.stockguard.stock.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StockEntryCreatedEvent(
        UUID movementId,
        UUID productId,
        UUID userId,
        int quantity,
        List<String> serialNumbers,
        Instant occurredAt
) {
    public StockEntryCreatedEvent {
        occurredAt = occurredAt != null ? occurredAt : Instant.now();
    }
}