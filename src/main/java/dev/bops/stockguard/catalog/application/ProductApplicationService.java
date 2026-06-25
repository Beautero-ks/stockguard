package dev.bops.stockguard.catalog.application;

import dev.bops.stockguard.catalog.application.dto.CreateProductCommand;
import dev.bops.stockguard.catalog.application.dto.ProductResponse;
import dev.bops.stockguard.catalog.application.dto.UpdateProductCommand;
import dev.bops.stockguard.catalog.domain.Product;
import dev.bops.stockguard.catalog.domain.ProductRepository;
import dev.bops.stockguard.traceability.application.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductApplicationService {

    private final ProductRepository productRepository;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    public ProductResponse create(CreateProductCommand command) {
        // Validation métier : unicité de la référence
        if (productRepository.existsByReference(command.reference())) {
            throw new IllegalArgumentException("Une pièce avec la référence " + command.reference() + " existe déjà");
        }

        // Création de l'agrégat (le domaine valide les règles métier)
        Product product = new Product(
                command.reference(),
                command.name(),
                command.location(),
                command.minStockThreshold(),
                command.maxStockThreshold()
        );

        // Persistance
        Product saved = productRepository.save(product);
        auditService.log(
                extractUserId(), "PRODUCT_CREATED", "Product", saved.getId(),
                null, saved.getReference() + " - " + saved.getName()
        );

        // Retour du DTO de réponse
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(UUID id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé : " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllActive() {
        return productRepository.findAllActive().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse update(UpdateProductCommand command) {
        Product product = productRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé : " + command.id()));

        // Appel à la méthode métier du domaine (pas de setters !)
        product.update(
                command.name(),
                command.description(),
                command.location(),
                command.minStockThreshold(),
                command.maxStockThreshold()
        );

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public void deactivate(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé : " + id));

        product.deactivate();
        productRepository.save(product);
        auditService.log(
                extractUserId(), "PRODUCT_DEACTIVATED", "Product", id,
                "active=true", "active=false"
        );
        product.drainDomainEvents().forEach(eventPublisher::publishEvent);
    }

    // --- Mapper (pour l'instant manuel, on passera à MapStruct plus tard) ---
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
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

    private UUID extractUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
