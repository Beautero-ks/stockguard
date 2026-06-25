package dev.bops.stockguard.traceability.infrastructure.persistence;

import dev.bops.stockguard.traceability.domain.AuditLog;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
class AuditLogJpaEntity {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String action;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "created_at")
    private Instant createdAt;

    static AuditLogJpaEntity fromDomain(AuditLog log) {
        return new AuditLogJpaEntity(
                log.getId(), log.getUserId(), log.getAction(),
                log.getEntityType(), log.getEntityId(),
                log.getOldValue(), log.getNewValue(), log.getCreatedAt()
        );
    }

    AuditLog toDomain() {
        return new AuditLog(id, userId, action, entityType, entityId,
                oldValue, newValue, createdAt);
    }
}