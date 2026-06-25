package dev.bops.stockguard.traceability.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class AuditLog {

    private final UUID id;
    private final UUID userId;
    private final String action;
    private final String entityType;
    private final UUID entityId;
    private final String oldValue;
    private final String newValue;
    private final Instant createdAt;

    public AuditLog(UUID userId, String action, String entityType, UUID entityId,
                    String oldValue, String newValue) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.createdAt = Instant.now();
    }

    // Reconstruction
    public AuditLog(UUID id, UUID userId, String action, String entityType, UUID entityId,
                    String oldValue, String newValue, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.createdAt = createdAt;
    }
}