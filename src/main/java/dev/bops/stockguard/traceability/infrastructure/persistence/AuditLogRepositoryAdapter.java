package dev.bops.stockguard.traceability.infrastructure.persistence;

import dev.bops.stockguard.traceability.domain.AuditLog;
import dev.bops.stockguard.traceability.domain.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        return jpaRepository.save(AuditLogJpaEntity.fromDomain(auditLog)).toDomain();
    }

    @Override
    public List<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId) {
        return jpaRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId)
                .stream().map(AuditLogJpaEntity::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByDateRange(Instant start, Instant end) {
        return jpaRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end)
                .stream().map(AuditLogJpaEntity::toDomain).toList();
    }

    @Override
    public List<AuditLog> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(AuditLogJpaEntity::toDomain).toList();
    }
}