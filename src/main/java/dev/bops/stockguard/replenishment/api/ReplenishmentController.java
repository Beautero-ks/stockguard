package dev.bops.stockguard.replenishment.api;

import dev.bops.stockguard.replenishment.application.ReplenishmentService;
import dev.bops.stockguard.replenishment.application.dto.AlertResponse;
import dev.bops.stockguard.replenishment.domain.ReplenishmentAlert;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/replenishment")
@RequiredArgsConstructor
class ReplenishmentController {

    private final ReplenishmentService replenishmentService;

    @GetMapping("/alerts")
    public ResponseEntity<List<AlertResponse>> getOpenAlerts() {
        return ResponseEntity.ok(
                replenishmentService.getOpenAlerts().stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/alerts/all")
    public ResponseEntity<List<AlertResponse>> getAllAlerts() {
        return ResponseEntity.ok(
                replenishmentService.getAllAlerts().stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @PostMapping("/alerts/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resolveAlert(@PathVariable UUID id) {
        replenishmentService.resolveAlert(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/alerts/{id}/dismiss")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> dismissAlert(@PathVariable UUID id) {
        replenishmentService.dismissAlert(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> triggerManually() {
        replenishmentService.checkAndGenerateAlerts();
        return ResponseEntity.ok("Analyse de réapprovisionnement déclenchée.");
    }

    private AlertResponse toResponse(ReplenishmentAlert alert) {
        return new AlertResponse(
                alert.getId(), alert.getProductId(), alert.getProductName(),
                alert.getCurrentStock(), alert.getMinThreshold(), alert.getMaxThreshold(),
                alert.getSuggestedQuantity(), alert.getStatus().name(),
                alert.getCreatedAt()
        );
    }
}