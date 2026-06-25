package dev.bops.stockguard.catalog.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ProductTest {
    @Test
    void shouldDeactivateProduct() {
        Product product = new Product("ELEC-001", "Résistance", "Tiroir A1", 10, 100);
        assertThat(product.isActive()).isTrue();

        product.deactivate();

        assertThat(product.isActive()).isFalse();

        var events = product.drainDomainEvents();

        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(ProductDeactivatedEvent.class);
    }

    @Test
    void shouldBeIdempotentOnMultipleDeactivate() {
        Product product = new Product("ELEC-001", "Résistance", "Tiroir A1", 10, 100);

        product.deactivate(); // 1ère fois : OK
        assertThat(product.isActive()).isFalse();
        assertThat(product.drainDomainEvents()).hasSize(1); // Événement généré

        product.deactivate(); // 2ème fois : idempotent
        assertThat(product.isActive()).isFalse();
        assertThat(product.drainDomainEvents()).isEmpty(); // Aucun nouvel événement
    }

    @Test
    void shouldRejectInvalidThresholds() {
        assertThatThrownBy(() -> new Product("ELEC-001", "Résistance", "T1", -1, 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("négatif");

        assertThatThrownBy(() -> new Product("ELEC-001", "Résistance", "T1", 100, 50))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximum");

    }
}
