package dev.bops.stockguard.catalog.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {
    Optional<ProductJpaEntity> findByReference(String reference);
    List<ProductJpaEntity> findByActiveTrue();
    boolean existsByReference(String reference);
}