package dev.bops.stockguard.replenishment.domain;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReplenishmentAlert {

    private final UUID id;
    private final UUID productId;
    private final String productName;  // Dénormalisé pour lisibilité
    private final int currentStock;
    private final int minThreshold;
    private final int maxThreshold;
    private final int suggestedQuantity;
    private AlertStatus status;
    private Instant resolvedAt;
    private final Instant createdAt;

    public ReplenishmentAlert(UUID productId, String productName, int currentStock,
                              int minThreshold, int maxThreshold) {
        Preconditions.checkArgument(productId != null, "Produit obligatoire");
        Preconditions.checkArgument(currentStock < minThreshold,
                "L'alerte n'est déclenchée que si stock < seuil minimum");
        Preconditions.checkArgument(maxThreshold > minThreshold,
                "Le seuil maximum doit être supérieur au minimum");

        this.id = UUID.randomUUID();
        this.productId = productId;
        this.productName = productName;
        this.currentStock = currentStock;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        this.suggestedQuantity = maxThreshold - currentStock;
        this.status = AlertStatus.OPEN;
        this.resolvedAt = null;
        this.createdAt = Instant.now();
    }

    // Reconstruction
    public ReplenishmentAlert(UUID id, UUID productId, String productName, int currentStock,
                              int minThreshold, int maxThreshold, int suggestedQuantity,
                              AlertStatus status, Instant resolvedAt, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.currentStock = currentStock;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        this.suggestedQuantity = suggestedQuantity;
        this.status = status;
        this.resolvedAt = resolvedAt;
        this.createdAt = createdAt;
    }

    public void resolve() {
        if (this.status == AlertStatus.RESOLVED) {
            return; // Idempotent
        }
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = Instant.now();
    }

    public void dismiss() {
        if (this.status != AlertStatus.OPEN) {
            throw new IllegalStateException("Seule une alerte ouverte peut être ignorée");
        }
        this.status = AlertStatus.DISMISSED;
        this.resolvedAt = Instant.now();
    }

    public boolean isOpen() {
        return this.status == AlertStatus.OPEN;
    }

    public enum AlertStatus {
        OPEN, RESOLVED, DISMISSED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplenishmentAlert that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}