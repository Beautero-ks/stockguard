package dev.bops.stockguard.catalog.domain;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Agrégat racine du contexte Catalogue.
 * Cette classe représente un produit dans notre domaine métier.
 * Elle est indépendante de JPA, de Spring, et de toute technologie.
 */
@Getter
public class Product {
    private List<Object> domainEvents = new ArrayList<>();

    private final UUID id;
    private String reference;
    private String name;
    private String description;
    private String location;
    private int minStockThreshold;
    private int maxStockThreshold;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    // Constructeur pour la création d'un nouveau produit
    public Product(String reference, String name, String location, int minStockThreshold, int maxStockThreshold) {
        Preconditions.checkArgument(reference != null && !reference.isBlank(), "La référence est obligatoire");
        Preconditions.checkArgument(name != null && !name.isBlank(), "Le nom est obligatoire");
        Preconditions.checkArgument(minStockThreshold >= 0, "Le seuil minimum ne peut pas être négatif");
        Preconditions.checkArgument(maxStockThreshold >= minStockThreshold, "Le seuil maximum doit être >= au seuil minimum");

        this.domainEvents = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.reference = reference;
        this.name = name;
        this.description = null;
        this.location = location;
        this.minStockThreshold = minStockThreshold;
        this.maxStockThreshold = maxStockThreshold;
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Constructeur pour la reconstruction depuis la persistance (pas de validation, ni génération d'ID)
    public Product(UUID id, String reference, String name, String description, String location,
                   int minStockThreshold, int maxStockThreshold, boolean active,
                   Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.reference = reference;
        this.name = name;
        this.description = description;
        this.location = location;
        this.minStockThreshold = minStockThreshold;
        this.maxStockThreshold = maxStockThreshold;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        this.domainEvents = new ArrayList<>();
    }

    // Méthodes métier (comportement)

    /**
     * Met à jour les informations modifiables du produit.
     */
    public void update(String name, String description, String location, int minStockThreshold, int maxStockThreshold) {
        Preconditions.checkArgument(minStockThreshold >= 0, "Le seuil minimum ne peut pas être négatif");
        Preconditions.checkArgument(maxStockThreshold >= minStockThreshold, "Le seuil maximum doit être >= au seuil minimum");
        this.name = name;
        this.description = description;
        this.location = location;
        this.minStockThreshold = minStockThreshold;
        this.maxStockThreshold = maxStockThreshold;
        this.updatedAt = Instant.now();
    }

    /**
     * Désactive le produit (opération idempotente).
     * Génère un événement de domaine uniquement lors de la première désactivation.
     */
    public void deactivate() {
        if (!this.active) {
            return; // Idempotent : déjà désactivé, rien à faire
        }
        this.active = false;
        this.updatedAt = Instant.now();
        // On enregistre l'événement pour traitement ultérieur
        this.domainEvents.add(new ProductDeactivatedEvent(this.id, this.updatedAt));
    }

    /**
     * Récupère et vide les événements de domaine en attente.
     * L'appel consomme les événements : un second appel retournera toujours une liste vide.
     */
    public List<Object> drainDomainEvents() {
        if (domainEvents.isEmpty()) {
            return List.of();
        }
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    // equals et hashCode basés sur l'ID (identité de l'entité)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}