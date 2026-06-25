package dev.bops.stockguard.user.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserCommand(
        @NotBlank @Email
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String password,

        @NotBlank
        String fullName,

        @NotBlank
        String role  // "ADMIN" ou "COMMERCIAL"
) {}