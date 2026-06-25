package dev.bops.stockguard.traceability.application;

import dev.bops.stockguard.stock.domain.event.StockEntryCreatedEvent;
import dev.bops.stockguard.stock.domain.event.StockExitCreatedEvent;
import dev.bops.stockguard.traceability.domain.AuditLogRepository;
import dev.bops.stockguard.traceability.domain.AuditLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AuditLogRepository auditLogRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onStockEntry(StockEntryCreatedEvent event) {
        AuditLog logs = new AuditLog(
                event.userId(),
                "STOCK_ENTRY",
                "StockMovement",
                event.movementId(),
                null,
                "Produit=" + event.productId() + ", Qté=" + event.quantity() +
                        ", Séries=" + String.join(",", event.serialNumbers())
        );
        auditLogRepository.save(logs);
        log.info("✅ [AUDIT] Entrée de stock tracée : {}", event.movementId());
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onStockExit(StockExitCreatedEvent event) {
        AuditLog logs = new AuditLog(
                event.userId(),
                "STOCK_EXIT",
                "StockMovement",
                event.movementId(),
                null,
                "Produit=" + event.productId() + ", Série=" + event.serialNumber() +
                        ", Client=" + (event.clientId() != null ? event.clientId() : "N/A")
        );
        auditLogRepository.save(logs);
        log.info("✅ [AUDIT] Sortie de stock tracée : {}", event.movementId());
    }
}