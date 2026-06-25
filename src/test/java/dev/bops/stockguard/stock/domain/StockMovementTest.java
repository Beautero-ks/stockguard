package dev.bops.stockguard.stock.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class StockMovementTest {

    @Test
    void shouldCreateEntryWithSerials() {
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        List<String> serials = List.of("SN-001", "SN-002", "SN-003");

        StockMovement entry = StockMovement.createEntry(productId, userId, "Réception cmd #123", serials);

        assertThat(entry.isEntry()).isTrue();
        assertThat(entry.getQuantity()).isEqualTo(3);
        assertThat(entry.getSerialNumbers()).hasSize(3);
        assertThat(entry.getSerialNumbers().get(0).isInStock()).isTrue();
    }

    @Test
    void shouldNotAllowExitIfSerialNotInStock() {
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        SerialNumber serial = new SerialNumber("SN-001", productId, UUID.randomUUID());
        serial.markAsSold(UUID.randomUUID()); // déjà vendu

        assertThatThrownBy(() -> StockMovement.createExit(productId, userId, "Vente", serial))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("n'est pas en stock");
    }
}