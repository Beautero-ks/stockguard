package dev.bops.stockguard.catalog.infrastructure.persistence;

import dev.bops.stockguard.catalog.domain.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ProductJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @Column(name = "min_stock_threshold", nullable = false)
    private int minStockThreshold;

    @Column(name = "max_stock_threshold", nullable = false)
    private int maxStockThreshold;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // --- Mappers : Conversion Domain <-> JPA ---

    static ProductJpaEntity fromDomain(Product product) {
        return new ProductJpaEntity(
                product.getId(),
                product.getReference(),
                product.getName(),
                product.getDescription(),
                product.getLocation(),
                product.getMinStockThreshold(),
                product.getMaxStockThreshold(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    Product toDomain() {
        return new Product(
                this.id,
                this.reference,
                this.name,
                this.description,
                this.location,
                this.minStockThreshold,
                this.maxStockThreshold,
                this.active,
                this.createdAt,
                this.updatedAt
        );
    }
}
