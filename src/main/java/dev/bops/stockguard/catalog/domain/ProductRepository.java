package dev.bops.stockguard.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de persistance pour l'agrégat Product.
 * Cette interface est définie dans le domaine, mais implémentée dans l'infrastructure.
 * C'est le principe d'inversion de dépendance (DIP de SOLID).
 */
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    Optional<Product> findByReference(String reference);
    List<Product> findAllActive();
    boolean existsByReference(String reference);
}