package dev.bops.stockguard.stock.infrastructure.persistence;

import dev.bops.stockguard.stock.domain.SerialNumber;
import dev.bops.stockguard.stock.domain.SerialNumber.SerialStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "serial_numbers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
class SerialNumberJpaEntity {

    @Id
    private UUID id;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SerialStatus status;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "entry_movement_id", nullable = false)
    private UUID entryMovementId;

    @Column(name = "exit_movement_id")
    private UUID exitMovementId;

    @Column(name = "client_id")
    private UUID clientId;

    @Column(name = "updated_at")
    private Instant updatedAt;

    static SerialNumberJpaEntity fromDomain(SerialNumber sn) {
        return new SerialNumberJpaEntity(
                sn.getId(), sn.getSerial(), sn.getStatus(), sn.getProductId(),
                sn.getEntryMovementId(), sn.getExitMovementId(), sn.getClientId(), sn.getUpdatedAt()
        );
    }

    SerialNumber toDomain() {
        return new SerialNumber(
                id, serial, status, productId,
                entryMovementId, exitMovementId, clientId, updatedAt
        );
    }
}
