package io.github.devopspro.stockguard.notification.application;

import dev.bops.stockguard.stock.domain.event.StockEntryCreatedEvent;
import dev.bops.stockguard.stock.domain.event.StockExitCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @Async  // Non-bloquant : ne ralentit pas la transaction métier
    @EventListener
    public void onStockEntry(StockEntryCreatedEvent event) {
        // Dans un vrai système, on enverrait un email/SMS ici
        log.info("📦 [NOTIFICATION] Nouvelle entrée de stock : produit={}, qté={}, séries={}",
                event.productId(), event.quantity(), event.serialNumbers());

        // Exemple : envoyer un email au responsable
        // emailService.send("responsable@stockguard.dev",
        //     "Entrée de stock",
        //     "Produit " + event.productId() + " : " + event.quantity() + " unités reçues");
    }

    @Async
    @EventListener
    public void onStockExit(StockExitCreatedEvent event) {
        log.info("📤 [NOTIFICATION] Sortie de stock : produit={}, série={}, client={}",
                event.productId(), event.serialNumber(),
                event.clientId() != null ? event.clientId() : "N/A");
    }
}