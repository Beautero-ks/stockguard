package dev.bops.stockguard.catalog.infrastructure.persistence;

import dev.bops.stockguard.catalog.domain.Product;
import dev.bops.stockguard.catalog.domain.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository jpaRepository;

    ProductRepositoryAdapter(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.fromDomain(product);
        ProductJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(ProductJpaEntity::toDomain);
    }

    @Override
    public Optional<Product> findByReference(String reference) {
        return jpaRepository.findByReference(reference).map(ProductJpaEntity::toDomain);
    }

    @Override
    public List<Product> findAllActive() {
        return jpaRepository.findByActiveTrue().stream()
                .map(ProductJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByReference(String reference) {
        return jpaRepository.existsByReference(reference);
    }
}