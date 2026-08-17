package com.efs.modules.transaction.controller;

import com.efs.modules.transaction.dto.TransactionRequest;
import com.efs.modules.transaction.dto.TransactionResponse;
import com.efs.modules.transaction.service.TransactionServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionServiceInterface transactionService;

    public TransactionController(
            TransactionServiceInterface transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request) {

        TransactionResponse response =
                transactionService.createTransaction(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable UUID transactionId) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(transactionId)
        );
    }

    @GetMapping("/reference/{transactionReference}")
    public ResponseEntity<TransactionResponse> getTransactionByReference(
            @PathVariable String transactionReference) {

        return ResponseEntity.ok(
                transactionService.getTransactionByReference(
                        transactionReference
                )
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TransactionResponse>>
    getTransactionsByCustomerId(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByCustomerId(customerId)
        );
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionRequest request) {

        return ResponseEntity.ok(
                transactionService.updateTransaction(
                        transactionId,
                        request
                )
        );
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable UUID transactionId) {

        transactionService.deleteTransaction(transactionId);

        return ResponseEntity.noContent().build();
    }
}