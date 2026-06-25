package dev.bops.stockguard.catalog.domain;

import java.time.Instant;
import java.util.UUID;

public record ProductDeactivatedEvent(UUID productId, Instant occurredAt){
    public ProductDeactivatedEvent(UUID productId) {
        this(productId, Instant.now());
    }
}
