package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionLocationRequest;
import com.efs.modules.transaction.dto.TransactionLocationResponse;
import com.efs.modules.transaction.service.TransactionLocationServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionLocationController {

    private final TransactionLocationServiceInterface
            transactionLocationService;

    public TransactionLocationController(
            TransactionLocationServiceInterface transactionLocationService) {

        this.transactionLocationService = transactionLocationService;
    }

    @PostMapping("/{transactionId}/locations")
    public ResponseEntity<TransactionLocationResponse> createLocation(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionLocationRequest request) {

        TransactionLocationResponse response =
                transactionLocationService.createLocation(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/locations/{locationId}")
    public ResponseEntity<TransactionLocationResponse> getLocationById(
            @PathVariable UUID locationId) {

        return ResponseEntity.ok(
                transactionLocationService
                        .getLocationById(locationId)
        );
    }

    @GetMapping("/{transactionId}/locations")
    public ResponseEntity<List<TransactionLocationResponse>>
    getLocationsByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionLocationService
                        .getLocationsByTransactionId(transactionId)
        );
    }

    @GetMapping("/locations/ip/{ipAddress}")
    public ResponseEntity<List<TransactionLocationResponse>>
    getLocationsByIpAddress(
            @PathVariable String ipAddress) {

        return ResponseEntity.ok(
                transactionLocationService
                        .getLocationsByIpAddress(ipAddress)
        );
    }

    @GetMapping("/locations/country/{countryCode}")
    public ResponseEntity<List<TransactionLocationResponse>>
    getLocationsByCountryCode(
            @PathVariable String countryCode) {

        return ResponseEntity.ok(
                transactionLocationService
                        .getLocationsByCountryCode(countryCode)
        );
    }

    @GetMapping("/locations/asn/{asn}")
    public ResponseEntity<List<TransactionLocationResponse>>
    getLocationsByAsn(
            @PathVariable Long asn) {

        return ResponseEntity.ok(
                transactionLocationService
                        .getLocationsByAsn(asn)
        );
    }
}