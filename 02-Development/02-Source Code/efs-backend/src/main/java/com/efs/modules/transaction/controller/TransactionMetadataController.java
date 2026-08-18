package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionMetadataRequest;
import com.efs.modules.transaction.dto.TransactionMetadataResponse;
import com.efs.modules.transaction.service.TransactionMetadataServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionMetadataController {

    private final TransactionMetadataServiceInterface transactionMetadataService;

    public TransactionMetadataController(
            TransactionMetadataServiceInterface transactionMetadataService) {

        this.transactionMetadataService = transactionMetadataService;
    }

    @PostMapping("/{transactionId}/metadata")
    public ResponseEntity<TransactionMetadataResponse> createMetadata(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionMetadataRequest request) {

        TransactionMetadataResponse response =
                transactionMetadataService.createMetadata(
                        transactionId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/metadata/{metadataId}")
    public ResponseEntity<TransactionMetadataResponse> getMetadataById(
            @PathVariable UUID metadataId) {

        return ResponseEntity.ok(
                transactionMetadataService
                        .getMetadataById(metadataId)
        );
    }

    @GetMapping("/{transactionId}/metadata")
    public ResponseEntity<List<TransactionMetadataResponse>>
    getMetadataByTransactionId(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionMetadataService
                        .getMetadataByTransactionId(transactionId)
        );
    }

    @GetMapping("/metadata/type/{metadataType}")
    public ResponseEntity<List<TransactionMetadataResponse>>
    getMetadataByType(
            @PathVariable String metadataType) {

        return ResponseEntity.ok(
                transactionMetadataService
                        .getMetadataByType(metadataType)
        );
    }
}