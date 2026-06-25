package dev.bops.stockguard.stock.application.dto;

import java.time.Instant;
import java.util.UUID;

public record SerialNumberResponse(
        UUID id,
        String serial,
        String status,
        Instant updatedAt
) {}
