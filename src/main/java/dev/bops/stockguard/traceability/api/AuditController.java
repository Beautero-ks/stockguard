package dev.bops.stockguard.traceability.api;

import dev.bops.stockguard.traceability.domain.AuditLog;
import dev.bops.stockguard.traceability.domain.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audit")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getEntityHistory(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {
        return ResponseEntity.ok(auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getUserActions(@PathVariable UUID userId) {
        return ResponseEntity.ok(auditLogRepository.findByUserId(userId));
    }

    @GetMapping("/period")
    public ResponseEntity<List<AuditLog>> getActionsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return ResponseEntity.ok(auditLogRepository.findByDateRange(start, end));
    }
}