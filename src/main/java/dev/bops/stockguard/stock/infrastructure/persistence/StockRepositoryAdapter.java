package dev.bops.stockguard.stock.infrastructure.persistence;

import dev.bops.stockguard.stock.domain.SerialNumber;
import dev.bops.stockguard.stock.domain.StockMovement;
import dev.bops.stockguard.stock.domain.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class StockRepositoryAdapter implements StockRepository {

    private final StockJpaRepository stockJpaRepository;
    private final SerialNumberJpaRepository serialJpaRepository;

    @Override
    @Transactional
    public StockMovement saveMovement(StockMovement movement) {
        StockMovementJpaEntity movementEntity = StockMovementJpaEntity.fromDomain(movement);
        StockMovementJpaEntity savedMovement = stockJpaRepository.save(movementEntity);

        List<SerialNumberJpaEntity> serialEntities = movement.getSerialNumbers().stream()
                .map(SerialNumberJpaEntity::fromDomain)
                .toList();
        serialJpaRepository.saveAll(serialEntities);

        List<SerialNumber> savedSerials = serialEntities.stream()
                .map(SerialNumberJpaEntity::toDomain)
                .toList();

        return savedMovement.toDomain(savedSerials);
    }

    @Override
    public Optional<StockMovement> findMovementById(UUID id) {
        return stockJpaRepository.findById(id)
                .map(entity -> {
                    List<SerialNumber> serials = serialJpaRepository
                            .findByEntryMovementIdOrExitMovementId(entity.getId(), entity.getId())
                            .stream()
                            .map(SerialNumberJpaEntity::toDomain)
                            .toList();
                    return entity.toDomain(serials);
                });
    }

    @Override
    public List<StockMovement> findMovementsByProductId(UUID productId) {
        return stockJpaRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(entity -> {
                    List<SerialNumber> serials = serialJpaRepository
                            .findByEntryMovementIdOrExitMovementId(entity.getId(), entity.getId())
                            .stream()
                            .map(SerialNumberJpaEntity::toDomain)
                            .toList();
                    return entity.toDomain(serials);
                })
                .toList();
    }

    @Override
    public Optional<SerialNumber> findSerialByCode(String serial) {
        return serialJpaRepository.findBySerial(serial)
                .map(SerialNumberJpaEntity::toDomain);
    }

    @Override
    public List<SerialNumber> findActiveSerialsByProductId(UUID productId) {
        return serialJpaRepository.findActiveByProductId(productId)
                .stream()
                .map(SerialNumberJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsBySerial(String serial) {
        return serialJpaRepository.existsBySerial(serial);
    }

    @Override
    public List<StockMovement> findMovementsByProductIdAndDateRange(UUID productId, Instant start, Instant end) {
        return stockJpaRepository
                .findByProductIdAndCreatedAtBetweenOrderByCreatedAtDesc(productId, start, end)
                .stream()
                .map(entity -> {
                    List<SerialNumber> serials = serialJpaRepository
                            .findByEntryMovementIdOrExitMovementId(entity.getId(), entity.getId())
                            .stream()
                            .map(SerialNumberJpaEntity::toDomain)
                            .toList();
                    return entity.toDomain(serials);
                })
                .toList();
    }
}