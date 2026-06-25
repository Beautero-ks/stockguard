package dev.bops.stockguard.stock.domain;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Getter
public class StockMovement {

    private final UUID id;
    private final UUID productId;
    private final UUID userId;
    private final MovementType movementType;
    private final int quantity;
    private final String reason;
    private final List<SerialNumber> serialNumbers;
    private final Instant createdAt;

    // Constructeur pour une ENTRÉE de stock
    private StockMovement(UUID productId, UUID userId, String reason, List<String> serials) {
        Preconditions.checkArgument(productId != null, "Produit obligatoire");
        Preconditions.checkArgument(userId != null, "Utilisateur obligatoire");
        Preconditions.checkArgument(serials != null && !serials.isEmpty(), "Au moins un N° de série requis");

        this.id = UUID.randomUUID();
        this.productId = productId;
        this.userId = userId;
        this.movementType = MovementType.ENTRY;
        this.quantity = serials.size();
        this.reason = reason;
        this.serialNumbers = new ArrayList<>();
        this.createdAt = Instant.now();

        // Création des SerialNumber directement dans l'agrégat
        for (String serial : serials) {
            this.serialNumbers.add(new SerialNumber(serial, productId, this.id));
        }
    }

    // Constructeur pour une SORTIE de stock (un seul N° de série)
    private StockMovement(UUID productId, UUID userId, String reason, SerialNumber serialNumber, UUID clientId) {
        Preconditions.checkArgument(productId != null, "Produit obligatoire");
        Preconditions.checkArgument(userId != null, "Utilisateur obligatoire");
        Preconditions.checkArgument(serialNumber != null, "N° de série obligatoire");

        this.id = UUID.randomUUID();
        this.productId = productId;
        this.userId = userId;
        this.movementType = MovementType.EXIT;
        this.quantity = -1;
        this.reason = reason;
        this.serialNumbers = new ArrayList<>();
        this.serialNumbers.add(serialNumber);
        this.createdAt = Instant.now();

        // Appel du comportement métier sur le SerialNumber
        serialNumber.markAsSold(this.id, clientId);
    }

    // Reconstruction depuis la persistance
    public StockMovement(UUID id, UUID productId, UUID userId, MovementType movementType,
                         int quantity, String reason, List<SerialNumber> serialNumbers, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.reason = reason;
        this.serialNumbers = Collections.unmodifiableList(serialNumbers);
        this.createdAt = createdAt;
    }

    // --- Factory Methods (Pattern Factory) ---

    public static StockMovement createEntry(UUID productId, UUID userId, String reason, List<String> serials) {
        return new StockMovement(productId, userId, reason, serials);
    }

    public static StockMovement createExit(UUID productId, UUID userId, String reason, SerialNumber serialNumber) {
        return createExit(productId, userId, reason, serialNumber, null);
    }

    public static StockMovement createExit(UUID productId, UUID userId, String reason, SerialNumber serialNumber, UUID clientId) {
        return new StockMovement(productId, userId, reason, serialNumber, clientId);
    }

    public enum MovementType {
        ENTRY, EXIT
    }

    // --- Méthodes métier ---

    public boolean isEntry() {
        return movementType == MovementType.ENTRY;
    }

    public int getGlobalStockImpact() {
        return quantity; // positif pour ENTRY, négatif pour EXIT
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockMovement that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
