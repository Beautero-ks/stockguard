package dev.bops.stockguard.replenishment.infrastructure.persistence;

import dev.bops.stockguard.replenishment.domain.ReplenishmentAlert;
import dev.bops.stockguard.replenishment.domain.ReplenishmentAlert.AlertStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "replenishment_alerts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
class ReplenishmentAlertJpaEntity {

    @Id
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "current_stock")
    private int currentStock;

    @Column(name = "min_threshold")
    private int minThreshold;

    @Column(name = "max_threshold")
    private int maxThreshold;

    @Column(name = "suggested_quantity")
    private int suggestedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "created_at")
    private Instant createdAt;

    static ReplenishmentAlertJpaEntity fromDomain(ReplenishmentAlert alert) {
        return new ReplenishmentAlertJpaEntity(
                alert.getId(), alert.getProductId(), alert.getProductName(),
                alert.getCurrentStock(), alert.getMinThreshold(), alert.getMaxThreshold(),
                alert.getSuggestedQuantity(), alert.getStatus(),
                alert.getResolvedAt(), alert.getCreatedAt()
        );
    }

    ReplenishmentAlert toDomain() {
        return new ReplenishmentAlert(
                id, productId, productName, currentStock, minThreshold, maxThreshold,
                suggestedQuantity, status, resolvedAt, createdAt
        );
    }
}