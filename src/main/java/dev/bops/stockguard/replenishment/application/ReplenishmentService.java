package dev.bops.stockguard.replenishment.application;

import dev.bops.stockguard.catalog.domain.Product;
import dev.bops.stockguard.catalog.domain.ProductRepository;
import dev.bops.stockguard.replenishment.domain.ReplenishmentAlert;
import dev.bops.stockguard.replenishment.domain.ReplenishmentRepository;
import dev.bops.stockguard.stock.domain.StockRepository;
import dev.bops.stockguard.traceability.application.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplenishmentService {

    private final AuditService auditService;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ReplenishmentRepository replenishmentRepository;

    /**
     * Algorithme de réapprovisionnement intelligent.
     * Exécuté périodiquement (ou sur demande).
     * Pour chaque produit actif, vérifie si le stock réel est sous le seuil minimum.
     * Si oui, crée une alerte (sauf si une alerte ouverte existe déjà).
     */
    @Transactional
    @Scheduled(cron = "0 0 8 * * MON-FRI") // Tous les jours ouvrés à 8h
    public void checkAndGenerateAlerts() {
        log.info("Démarrage de l'analyse de réapprovisionnement...");
        List<Product> activeProducts = productRepository.findAllActive();
        int alertsGenerated = 0;

        for (Product product : activeProducts) {
            // Calcul du stock réel = nombre de N° de série IN_STOCK
            int actualStock = stockRepository.findActiveSerialsByProductId(product.getId()).size();

            // Règle métier : le stock est-il sous le seuil minimum ?
            if (actualStock < product.getMinStockThreshold()) {
                // Évite les doublons : ne crée pas d'alerte s'il en existe déjà une ouverte
                boolean alreadyAlerted = replenishmentRepository
                        .findOpenAlertByProductId(product.getId())
                        .isPresent();

                if (!alreadyAlerted) {
                    ReplenishmentAlert alert = new ReplenishmentAlert(
                            product.getId(),
                            product.getName(),
                            actualStock,
                            product.getMinStockThreshold(),
                            product.getMaxStockThreshold()
                    );
                    replenishmentRepository.save(alert);
                    auditService.log(
                            getSystemUserId(), "REPLENISHMENT_ALERT_CREATED", "ReplenishmentAlert", alert.getId(),
                            null, "Produit=" + product.getReference() + ", Stock=" + actualStock +
                                    ", Suggestion=" + alert.getSuggestedQuantity()
                    );
                    alertsGenerated++;
                    log.info("Alerte créée pour {} : stock={}, min={}, suggestion={}",
                            product.getReference(), actualStock, product.getMinStockThreshold(),
                            alert.getSuggestedQuantity());
                }
            }
        }
        log.info("Analyse terminée. {} alerte(s) générée(s).", alertsGenerated);
    }

    @Transactional(readOnly = true)
    public List<ReplenishmentAlert> getOpenAlerts() {
        return replenishmentRepository.findOpenAlerts();
    }

    @Transactional(readOnly = true)
    public List<ReplenishmentAlert> getAllAlerts() {
        return replenishmentRepository.findAll();
    }

    @Transactional
    public void resolveAlert(UUID alertId) {
        ReplenishmentAlert alert = replenishmentRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alerte non trouvée"));
        alert.resolve();
        replenishmentRepository.save(alert);
    }

    @Transactional
    public void dismissAlert(UUID alertId) {
        ReplenishmentAlert alert = replenishmentRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alerte non trouvée"));
        alert.dismiss();
        replenishmentRepository.save(alert);

    }

    // Méthode helper pour l'utilisateur système
    private UUID getSystemUserId() {
        // Retourne un ID fixe pour le système, ou l'utilisateur courant si dispo
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UUID userId) {
            return userId;
        }
        return UUID.fromString("00000000-0000-0000-0000-000000000000"); // Système
    }
}