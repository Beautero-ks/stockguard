package dev.bops.stockguard.catalog.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductCommand(
        @NotBlank(message = "La référence est obligatoire")
        String reference,

        @NotBlank(message = "Le nom est obligatoire")
        String name,

        String description,

        String location,

        @PositiveOrZero(message = "Le seuil minimum doit être >= 0")
        int minStockThreshold,

        @PositiveOrZero(message = "Le seuil maximum doit être >= 0")
        int maxStockThreshold
) {}