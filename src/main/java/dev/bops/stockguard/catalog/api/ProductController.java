package dev.bops.stockguard.catalog.api;

import dev.bops.stockguard.catalog.application.ProductApplicationService;
import dev.bops.stockguard.catalog.application.dto.CreateProductCommand;
import dev.bops.stockguard.catalog.application.dto.ProductResponse;
import dev.bops.stockguard.catalog.application.dto.UpdateProductCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
class ProductController {

    private final ProductApplicationService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductCommand command) {
        ProductResponse response = productService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAllActive() {
        return ResponseEntity.ok(productService.findAllActive());
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateProductCommand command) {
        // On s'assure que l'ID du path correspond à celui du body
        if (!id.equals(command.id())) {
            throw new IllegalArgumentException("L'ID dans le chemin et dans le corps ne correspondent pas");
        }
        return ResponseEntity.ok(productService.update(command));
    }

    @DeleteMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        productService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}