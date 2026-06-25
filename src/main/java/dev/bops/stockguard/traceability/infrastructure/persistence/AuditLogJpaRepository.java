package dev.bops.stockguard.traceability.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, UUID> {
    List<AuditLogJpaEntity> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, UUID entityId);
    List<AuditLogJpaEntity> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant start, Instant end);
    List<AuditLogJpaEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}