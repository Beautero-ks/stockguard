package dev.bops.stockguard.stock.api;

import dev.bops.stockguard.stock.application.StockApplicationService;
import dev.bops.stockguard.stock.application.dto.EnterStockCommand;
import dev.bops.stockguard.stock.application.dto.ExitStockCommand;
import dev.bops.stockguard.stock.application.dto.StockMovementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
class StockController {

    private final StockApplicationService stockService;

    @PostMapping("/entry")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockMovementResponse> enterStock(@Valid @RequestBody EnterStockCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.enterStock(command));
    }

    @PostMapping("/exit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockMovementResponse> exitStock(@Valid @RequestBody ExitStockCommand command) {
        return ResponseEntity.ok(stockService.exitStock(command));
    }

    @GetMapping("/movements/{productId}")
    public ResponseEntity<List<StockMovementResponse>> getMovements(
            @PathVariable UUID productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {

        if (start != null && end != null) {
            return ResponseEntity.ok(stockService.getMovementsByDateRange(productId, start, end));
        }
        return ResponseEntity.ok(stockService.getMovements(productId));
    }
}