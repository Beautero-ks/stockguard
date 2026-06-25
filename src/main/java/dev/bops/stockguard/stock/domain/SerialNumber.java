package dev.bops.stockguard.stock.domain;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class SerialNumber {

    private final UUID id;
    private final String serial;
    private SerialStatus status;
    private final UUID productId;
    private final UUID entryMovementId;
    private UUID exitMovementId;
    private Instant updatedAt;

    // Création (entrée en stock)
    SerialNumber(String serial, UUID productId, UUID entryMovementId) {
        Preconditions.checkArgument(serial != null && !serial.isBlank(), "Le numéro de série est obligatoire");
        this.id = UUID.randomUUID();
        this.serial = serial;
        this.status = SerialStatus.IN_STOCK;
        this.productId = productId;
        this.entryMovementId = entryMovementId;
        this.exitMovementId = null;
        this.updatedAt = Instant.now();
    }

    // Reconstruction (depuis la persistance)
    public SerialNumber(UUID id, String serial, SerialStatus status, UUID productId,
                        UUID entryMovementId, UUID exitMovementId, Instant updatedAt) {
        this.id = id;
        this.serial = serial;
        this.status = status;
        this.productId = productId;
        this.entryMovementId = entryMovementId;
        this.exitMovementId = exitMovementId;
        this.updatedAt = updatedAt;
    }

    // Comportement : sortir du stock
    void markAsSold(UUID exitMovementId) {
        if (this.status != SerialStatus.IN_STOCK) {
            throw new IllegalStateException("Le N° de série " + serial + " n'est pas en stock. Statut actuel : " + status);
        }
        this.status = SerialStatus.SOLD;
        this.exitMovementId = exitMovementId;
        this.updatedAt = Instant.now();
    }

    boolean isInStock() {
        return this.status == SerialStatus.IN_STOCK;
    }

    public enum SerialStatus {
        IN_STOCK, SOLD, RETURNED, DEFECTIVE
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SerialNumber that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}