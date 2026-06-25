package dev.bops.stockguard.catalog.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record UpdateProductCommand(
        UUID id,

        @NotBlank(message = "Le nom est obligatoire")
        String name,

        String description,

        String location,

        @PositiveOrZero
        int minStockThreshold,

        @PositiveOrZero
        int maxStockThreshold
) {}