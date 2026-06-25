package dev.bops.stockguard.stock.infrastructure.persistence;

import dev.bops.stockguard.stock.domain.SerialNumber;
import dev.bops.stockguard.stock.domain.StockMovement;
import dev.bops.stockguard.stock.domain.StockMovement.MovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
class StockMovementJpaEntity {

    @Id
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(nullable = false)
    private int quantity;

    private String reason;

    @Column(name = "created_at")
    private Instant createdAt;

    static StockMovementJpaEntity fromDomain(StockMovement movement) {
        return new StockMovementJpaEntity(
                movement.getId(), movement.getProductId(), movement.getUserId(),
                movement.getMovementType(), movement.getQuantity(),
                movement.getReason(), movement.getCreatedAt()
        );
    }

    StockMovement toDomain(List<SerialNumber> serialNumbers) {
        return new StockMovement(
                id, productId, userId, movementType, quantity, reason, serialNumbers, createdAt
        );
    }
}