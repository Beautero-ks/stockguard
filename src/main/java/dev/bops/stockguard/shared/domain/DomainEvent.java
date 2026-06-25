package dev.bops.stockguard.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Interface de base pour tous les événements de domaine.
 */
public interface DomainEvent {
    UUID getEventId();
    Instant getOccurredAt();
}