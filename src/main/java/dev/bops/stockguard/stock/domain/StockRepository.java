package dev.bops.stockguard.stock.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository {
    StockMovement saveMovement(StockMovement movement);
    Optional<StockMovement> findMovementById(UUID id);
    List<StockMovement> findMovementsByProductId(UUID productId);
    Optional<SerialNumber> findSerialByCode(String serial);
    List<SerialNumber> findActiveSerialsByProductId(UUID productId);
    boolean existsBySerial(String serial);

    List<StockMovement> findMovementsByProductIdAndDateRange(UUID productId, Instant start, Instant end);
}