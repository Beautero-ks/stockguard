package dev.bops.stockguard.traceability.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId);
    List<AuditLog> findByDateRange(Instant start, Instant end);
    List<AuditLog> findByUserId(UUID userId);
}