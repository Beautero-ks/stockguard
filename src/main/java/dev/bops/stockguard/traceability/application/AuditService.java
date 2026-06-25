package dev.bops.stockguard.traceability.application;

import dev.bops.stockguard.traceability.domain.AuditLog;
import dev.bops.stockguard.traceability.domain.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * REQUIRES_NEW : l'audit est sauvegardé même si la transaction métier échoue.
     * C'est crucial pour tracer les tentatives échouées.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(UUID userId, String action, String entityType, UUID entityId,
                    String oldValue, String newValue) {
        AuditLog auditLog = new AuditLog(userId, action, entityType, entityId, oldValue, newValue);
        auditLogRepository.save(auditLog);
    }
}