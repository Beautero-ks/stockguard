package dev.bops.stockguard.replenishment.infrastructure.persistence;

import dev.bops.stockguard.replenishment.domain.ReplenishmentAlert.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ReplenishmentJpaRepository extends JpaRepository<ReplenishmentAlertJpaEntity, UUID> {
    List<ReplenishmentAlertJpaEntity> findByStatusOrderByCreatedAtDesc(AlertStatus status);
    Optional<ReplenishmentAlertJpaEntity> findByProductIdAndStatus(UUID productId, AlertStatus status);
}