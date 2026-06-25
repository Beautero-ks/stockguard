package dev.bops.stockguard.stock.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ExitStockCommand(
        @NotNull(message = "L'ID du produit est obligatoire")
        UUID productId,

        @NotNull(message = "L'ID utilisateur est obligatoire")
        UUID userId,

        String reason,

        @NotBlank(message = "Le numéro de série est obligatoire")
        String serialNumber,

        UUID clientId  // Optionnel, donc pas de validation
) {}