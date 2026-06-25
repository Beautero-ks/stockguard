package dev.bops.stockguard.stock.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record EnterStockCommand(
        @NotNull(message = "L'ID du produit est obligatoire")
        UUID productId,
        @NotNull(message = "L'ID de l'utilisateur est obligatoire")
        UUID userId,
        String reason,
        @NotEmpty(message = "Au moins un numéro de série est requis")
        @NotEmpty List<@NotNull(message = "Le numéro de série ne peut pas être null") String> serialNumbers
) {}