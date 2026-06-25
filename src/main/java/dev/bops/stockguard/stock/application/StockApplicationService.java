package dev.bops.stockguard.stock.application;

import dev.bops.stockguard.catalog.domain.ProductRepository;
import dev.bops.stockguard.stock.application.dto.EnterStockCommand;
import dev.bops.stockguard.stock.application.dto.ExitStockCommand;
import dev.bops.stockguard.stock.application.dto.SerialNumberResponse;
import dev.bops.stockguard.stock.application.dto.StockMovementResponse;
import dev.bops.stockguard.stock.domain.SerialNumber;
import dev.bops.stockguard.stock.domain.StockMovement;
import dev.bops.stockguard.stock.domain.StockRepository;
import dev.bops.stockguard.stock.domain.event.StockEntryCreatedEvent;
import dev.bops.stockguard.stock.domain.event.StockExitCreatedEvent;
import dev.bops.stockguard.traceability.application.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StockApplicationService {

    private final AuditService auditService;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository; // Vérifie que le produit existe
    private final ApplicationEventPublisher eventPublisher;

    public StockMovementResponse enterStock(EnterStockCommand command) {
        // Validation : produit existe ?
        productRepository.findById(command.productId())
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        // Validation : pas de doublon de N° de série
        for (String serial : command.serialNumbers()) {
            if (stockRepository.existsBySerial(serial)) {
                throw new IllegalArgumentException("Le N° de série " + serial + " existe déjà");
            }
        }

        // Création de l'agrégat (le domaine valide les règles métier)
        StockMovement movement = StockMovement.createEntry(
                command.productId(),
                command.userId(),
                command.reason(),
                command.serialNumbers()
        );

        StockMovement saved = stockRepository.saveMovement(movement);
//        auditService.log(
//                command.userId(), "STOCK_ENTRY", "StockMovement", saved.getId(),
//                null, "Produit=" + command.productId() + ", Qté=" + command.serialNumbers().size() +
//                        ", Séries=" + String.join(",", command.serialNumbers())
//        );
        eventPublisher.publishEvent(new StockEntryCreatedEvent(
                saved.getId(), saved.getProductId(), saved.getUserId(),
                saved.getQuantity(), saved.getSerialNumbers().stream()
                .map(SerialNumber::getSerial).toList(),
                saved.getCreatedAt()
        ));
        return toResponse(saved);
    }

    public StockMovementResponse exitStock(ExitStockCommand command) {
        productRepository.findById(command.productId())
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        SerialNumber serial = stockRepository.findSerialByCode(command.serialNumber())
                .orElseThrow(() -> new IllegalArgumentException("N° de série non trouvé"));

        // L'agrégat valide que le N° de série est bien IN_STOCK
        StockMovement movement = StockMovement.createExit(
                command.productId(),
                command.userId(),
                command.reason(),
                serial
        );

        // TODO: Si clientId présent, on pourrait l'associer au SerialNumber pour la garantie
        // Cette fonctionnalité sera complétée dans le module traceability

        StockMovement saved = stockRepository.saveMovement(movement);
//        auditService.log(
//                command.userId(), "STOCK_EXIT", "StockMovement", saved.getId(),
//                null, "Produit=" + command.productId() + ", Série=" + command.serialNumber() +
//                        ", Client=" + (command.clientId() != null ? command.clientId() : "N/A")
//        );
        eventPublisher.publishEvent(new StockExitCreatedEvent(
                saved.getId(), saved.getProductId(), saved.getUserId(),
                command.serialNumber(), command.clientId(), saved.getCreatedAt()
        ));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<StockMovementResponse> getMovements(UUID productId) {
        return stockRepository.findMovementsByProductId(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    private StockMovementResponse toResponse(StockMovement movement) {
        List<SerialNumberResponse> serials = movement.getSerialNumbers().stream()
                .map(sn -> new SerialNumberResponse(sn.getId(), sn.getSerial(),
                        sn.getStatus().name(), sn.getUpdatedAt()))
                .toList();

        return new StockMovementResponse(
                movement.getId(), movement.getProductId(), movement.getUserId(),
                movement.getMovementType().name(), movement.getQuantity(),
                movement.getReason(), serials, movement.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<StockMovementResponse> getMovementsByDateRange(UUID productId, Instant start, Instant end) {
        return stockRepository.findMovementsByProductIdAndDateRange(productId, start, end).stream()
                .map(this::toResponse)
                .toList();
    }
}