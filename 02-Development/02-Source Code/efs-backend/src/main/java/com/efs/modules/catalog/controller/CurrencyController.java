package com.efs.modules.catalog.controller;

import com.efs.modules.catalog.dto.CurrencyRequest;
import com.efs.modules.catalog.dto.CurrencyResponse;
import com.efs.modules.catalog.service.CurrencyServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

    private final CurrencyServiceInterface currencyService;

    public CurrencyController(
            CurrencyServiceInterface currencyService) {

        this.currencyService =
                currencyService;
    }

    @PostMapping
    public ResponseEntity<CurrencyResponse> createCurrency(
            @Valid @RequestBody CurrencyRequest request) {

        CurrencyResponse response =
                currencyService.createCurrency(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{currencyId}")
    public ResponseEntity<CurrencyResponse> getCurrencyById(
            @PathVariable UUID currencyId) {

        return ResponseEntity.ok(
                currencyService.getCurrencyById(
                        currencyId
                )
        );
    }

    @GetMapping("/code/{currencyCode}")
    public ResponseEntity<CurrencyResponse> getCurrencyByCurrencyCode(
            @PathVariable String currencyCode) {

        return ResponseEntity.ok(
                currencyService.getCurrencyByCurrencyCode(
                        currencyCode
                )
        );
    }

    @GetMapping("/numeric/{numericCode}")
    public ResponseEntity<CurrencyResponse> getCurrencyByNumericCode(
            @PathVariable String numericCode) {

        return ResponseEntity.ok(
                currencyService.getCurrencyByNumericCode(
                        numericCode
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<CurrencyResponse>> getCurrencies(
            @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(
                    currencyService.getCurrenciesByStatus(
                            status
                    )
            );
        }

        return ResponseEntity.ok(
                currencyService.getAllCurrencies()
        );
    }
}