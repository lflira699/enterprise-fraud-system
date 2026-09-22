package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionMetadataRequest;
import com.efs.modules.transaction.dto.TransactionMetadataResponse;
import com.efs.modules.transaction.service.TransactionChildAccessServiceInterface;
import com.efs.modules.transaction.service.TransactionMetadataServiceInterface;
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
public class TransactionMetadataController {

    private final TransactionMetadataServiceInterface
            transactionMetadataService;

    private final TransactionChildAccessServiceInterface
            transactionChildAccessService;

    private final SecurityContextProvider securityContextProvider;

    public TransactionMetadataController(
            TransactionMetadataServiceInterface
                    transactionMetadataService,
            TransactionChildAccessServiceInterface
                    transactionChildAccessService,
            SecurityContextProvider securityContextProvider) {

        this.transactionMetadataService =
                transactionMetadataService;

        this.transactionChildAccessService =
                transactionChildAccessService;

        this.securityContextProvider =
                securityContextProvider;
    }

    @PostMapping("/{transactionId}/metadata")
    public ResponseEntity<TransactionMetadataResponse> createMetadata(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionMetadataRequest request) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        TransactionMetadataResponse response =
                transactionChildAccessService.create(
                        securityContext,
                        transactionId,
                        request,
                        transactionMetadataService::createMetadata
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/metadata/{metadataId}")
    public ResponseEntity<TransactionMetadataResponse> getMetadataById(
            @PathVariable UUID metadataId) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService.getById(
                        securityContext,
                        metadataId,
                        transactionMetadataService::getMetadataById,
                        TransactionMetadataResponse::getTransactionId,
                        "Transaction metadata not found: "
                                + metadataId
                )
        );
    }

    @GetMapping("/{transactionId}/metadata")
    public ResponseEntity<List<TransactionMetadataResponse>>
    getMetadataByTransactionId(
            @PathVariable UUID transactionId) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService.getByTransactionId(
                        securityContext,
                        transactionId,
                        transactionMetadataService
                                ::getMetadataByTransactionId
                )
        );
    }

    @GetMapping("/metadata/type/{metadataType}")
    public ResponseEntity<List<TransactionMetadataResponse>>
    getMetadataByType(
            @PathVariable String metadataType) {

        SecurityContext securityContext =
                securityContextProvider.getCurrentContext();

        return ResponseEntity.ok(
                transactionChildAccessService.filterVisible(
                        securityContext,
                        () ->
                                transactionMetadataService
                                        .getMetadataByType(metadataType),
                        TransactionMetadataResponse::getTransactionId
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