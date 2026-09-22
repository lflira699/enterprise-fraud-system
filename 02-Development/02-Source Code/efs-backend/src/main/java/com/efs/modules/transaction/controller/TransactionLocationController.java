package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionLocationRequest;
import com.efs.modules.transaction.dto.TransactionLocationResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionLocationServiceInterface;
import com.efs.shared.security.SecurityContext;
import com.efs.shared.security.SecurityContextProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionLocationController {

    private final TransactionLocationServiceInterface
            transactionLocationService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider
            securityContextProvider;

    public TransactionLocationController(
            TransactionLocationServiceInterface transactionLocationService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionLocationService =
                transactionLocationService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/locations")
    public ResponseEntity<TransactionLocationResponse> createLocation(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionLocationRequest request) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        TransactionLocationResponse response =
                transactionChildAccessService
                        .create(
                                securityContext,
                                transactionId,
                                request,
                                transactionLocationService::createLocation
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/locations/{locationId}")
    public ResponseEntity<TransactionLocationResponse> getLocationById(
            @PathVariable UUID locationId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getById(
                                securityContext,
                                locationId,
                                transactionLocationService::getLocationById,
                                TransactionLocationResponse::getTransactionId,
                                "Transaction location not found: "
                                        + locationId
                        )
        );
    }

    @GetMapping("/{transactionId}/locations")
    public ResponseEntity<List<TransactionLocationResponse>>
    getLocationsByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .getByTransactionId(
                                securityContext,
                                transactionId,
                                transactionLocationService
                                        ::getLocationsByTransactionId
                        )
        );
    }

    @GetMapping("/locations/ip/{ipAddress}")
    public ResponseEntity<List<TransactionLocationResponse>>
    getLocationsByIpAddress(
            @PathVariable String ipAddress) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionLocationService
                                                .getLocationsByIpAddress(
                                                        ipAddress
                                                ),
                                TransactionLocationResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/locations/country/{countryCode}")
    public ResponseEntity<List<TransactionLocationResponse>>
    getLocationsByCountryCode(
            @PathVariable String countryCode) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionLocationService
                                                .getLocationsByCountryCode(
                                                        countryCode
                                                ),
                                TransactionLocationResponse
                                        ::getTransactionId
                        )
        );
    }

    @GetMapping("/locations/asn/{asn}")
    public ResponseEntity<List<TransactionLocationResponse>>
    getLocationsByAsn(
            @PathVariable Long asn) {

        SecurityContext securityContext =
                securityContextProvider
                        .getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService
                        .filterVisible(
                                securityContext,
                                () ->
                                        transactionLocationService
                                                .getLocationsByAsn(
                                                        asn
                                                ),
                                TransactionLocationResponse
                                        ::getTransactionId
                        )
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Void> handleAccessDenied(
            AccessDeniedException exception) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }
}
