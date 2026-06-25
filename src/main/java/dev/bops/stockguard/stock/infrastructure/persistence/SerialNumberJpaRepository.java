package dev.bops.stockguard.stock.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface SerialNumberJpaRepository extends JpaRepository<SerialNumberJpaEntity, UUID> {
    Optional<SerialNumberJpaEntity> findBySerial(String serial);
    boolean existsBySerial(String serial);

    @Query("SELECT s FROM SerialNumberJpaEntity s WHERE s.productId = :productId AND s.status = 'IN_STOCK'")
    List<SerialNumberJpaEntity> findActiveByProductId(UUID productId);


    List<SerialNumberJpaEntity> findByEntryMovementIdOrExitMovementId(UUID entryMovementId, UUID exitMovementId);
}
