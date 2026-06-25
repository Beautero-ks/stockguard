package dev.bops.stockguard.replenishment.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReplenishmentRepository {
    ReplenishmentAlert save(ReplenishmentAlert alert);
    Optional<ReplenishmentAlert> findById(UUID id);
    List<ReplenishmentAlert> findOpenAlerts();
    List<ReplenishmentAlert> findAll();
    Optional<ReplenishmentAlert> findOpenAlertByProductId(UUID productId);
}