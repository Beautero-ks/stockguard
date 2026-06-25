package dev.bops.stockguard.stock.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

interface StockJpaRepository extends JpaRepository<StockMovementJpaEntity, UUID> {
    List<StockMovementJpaEntity> findByProductIdOrderByCreatedAtDesc(UUID productId);
//    List<StockMovementJpaEntity> findByProductIdAndCreatedAtBetweenOrderByCreatedAtDesc(
//            UUID productId, Instant start, Instant end
//    );
    Collection<StockMovementJpaEntity> findByProductIdAndCreatedAtBetweenOrderByCreatedAtDesc(UUID productId, Instant start, Instant end);
}