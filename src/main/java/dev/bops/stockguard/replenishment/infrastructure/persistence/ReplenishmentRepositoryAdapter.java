package dev.bops.stockguard.replenishment.infrastructure.persistence;

import dev.bops.stockguard.replenishment.domain.ReplenishmentAlert;
import dev.bops.stockguard.replenishment.domain.ReplenishmentAlert.AlertStatus;
import dev.bops.stockguard.replenishment.domain.ReplenishmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class ReplenishmentRepositoryAdapter implements ReplenishmentRepository {

    private final ReplenishmentJpaRepository jpaRepository;

    @Override
    public ReplenishmentAlert save(ReplenishmentAlert alert) {
        return jpaRepository.save(ReplenishmentAlertJpaEntity.fromDomain(alert)).toDomain();
    }

    @Override
    public Optional<ReplenishmentAlert> findById(UUID id) {
        return jpaRepository.findById(id).map(ReplenishmentAlertJpaEntity::toDomain);
    }

    @Override
    public List<ReplenishmentAlert> findOpenAlerts() {
        return jpaRepository.findByStatusOrderByCreatedAtDesc(AlertStatus.OPEN).stream()
                .map(ReplenishmentAlertJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<ReplenishmentAlert> findAll() {
        return jpaRepository.findAll().stream()
                .map(ReplenishmentAlertJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<ReplenishmentAlert> findOpenAlertByProductId(UUID productId) {
        return jpaRepository.findByProductIdAndStatus(productId, AlertStatus.OPEN)
                .map(ReplenishmentAlertJpaEntity::toDomain);
    }
}